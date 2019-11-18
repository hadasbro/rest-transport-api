package github.hadasbro.transport.controllers;

import github.hadasbro.transport.aspects.Logger;
import github.hadasbro.transport.config.SwaggerConfig;
import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.domain.transport.Vehicle;
import github.hadasbro.transport.events.OnOperatorChangeFlushCacheEvent;
import github.hadasbro.transport.exceptions.ValidationException;
import github.hadasbro.transport.requests.responses.MapResponse;
import github.hadasbro.transport.requests.responses.ResultResponse;
import github.hadasbro.transport.requests.responses.ResultResponseOperator;
import github.hadasbro.transport.requests.responses.ResultResponseSimple;
import github.hadasbro.transport.services.OperatorService;
import github.hadasbro.transport.utils.CollectionUtils;
import github.hadasbro.transport.utils.ObjectUtils;
import github.hadasbro.transport.validators.operator.ValidateOperatorCode;
import github.hadasbro.transport.webDto.ErrorDto;
import github.hadasbro.transport.webDto.OperatorByVehicleOwner;
import github.hadasbro.transport.webDto.OperatorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.service.ApiInfo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;

@SuppressWarnings({"unused", "WeakerAccess"})
@RestController
@RequestMapping(
        value = "/operator/"
)
/* CORS */
@CrossOrigin(
        origins = "*",
        allowedHeaders = "*",
        maxAge = 36000
)
class OperatorController extends BaseController {

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    /**
     * GroupBy
     *
     * groupping options
     *
     */
    private enum GroupBy {

        TypeName("typename"),
        Owner("owner");

        public String enumval;

        GroupBy(String i) {this.enumval = i;}

        public static boolean isAllowed(String v) {

           return CollectionUtils.anyOkFromCollection(
                   Arrays.asList(values()),
                   (ev) -> ev.enumval.equals(v)
           );

        }
    }

    /**
     * OperatorGroupperTask
     *
     * FORK - JOIN pattern
     *
     * this is Recursive task, needed to group
     * operators by vehicle and owner
     */
    private static class OperatorGroupperTask extends RecursiveTask<Map<String, Map<String, Set<OperatorDto>>>> {

        private Set<Vehicle> allVehicles;
        private Set<Vehicle> singleVehicle = new HashSet<>(1);
        private Set<Operator> allOperators;
        private Set<String> operatorCodes;

        private OperatorGroupperTask() {}

        /**
         * OperatorGroupperTask
         *
         * @param allVehicles -
         * @param allOperators -
         * @param operatorCodes -
         */
        public OperatorGroupperTask(
                Set<Vehicle> allVehicles,
                Set<Operator> allOperators,
                Set<String> operatorCodes
        ) {
            this.allVehicles = allVehicles;
            this.allOperators = allOperators;
            this.operatorCodes = operatorCodes;
        }

        private OperatorGroupperTask setSingleSubset(Set<Vehicle> singleVehicle) {
            this.singleVehicle = singleVehicle;
            return this;
        }

        @Override
        protected Map<String, Map<String, Set<OperatorDto>>> compute(){

            Collection<OperatorGroupperTask> col = new LinkedList<>();

            if(singleVehicle.isEmpty()) {

                /*
                split set with vehicles to single-element subsets
                and compute result for each of them, then join result when done
                 */

                for (Vehicle allVehicle : allVehicles) {
                    col.add(
                            (new OperatorGroupperTask(this.allVehicles, this.allOperators, this.operatorCodes))
                                    .setSingleSubset(new HashSet<>(1) {{
                                        add(allVehicle);
                                    }})
                    );
                }

                /*
                compute and reduce to Map<Vehicle <owner, Set<Operator>>>
                 */
                return ForkJoinTask
                        .invokeAll(col)
                        .stream()
                        .map(ForkJoinTask::join)
                        .filter(Objects::nonNull)
                        .reduce((m1, m2) -> {
                            // join all submaps
                            m1.putAll(m2);
                            return m1;
                        })
                        .orElse(null);

            } else {

                /*
                group operators per vehicle and owner and return map with result
                Map<String, Map<String, Set<OperatorDto>>>
                Map<operatorName, Map<VehicleName, Set<OperatorDto>>>
                 */

                return singleVehicle
                       .stream()
                       .collect(
                               toMap(
                                       Vehicle::getName,
                                       oper -> {

                                           // get operators from list insteaf use g.getOperators() to avoid many SQL queries
                                           // (preload needed operators and use entity's lazy-loading property)

                                           Set<Operator> operators =
                                                   allOperators
                                                           .parallelStream()
                                                           .filter(g -> operatorCodes.contains(g.getOwner().getSlug()))
                                                           .collect(Collectors.toSet());


                                           return CollectionUtils.collGroupper(
                                                   ObjectUtils.convertPojoToDto(OperatorDto.class, operators),
                                                   OperatorDto::getOwnerSlug
                                           );

                                       }
                               )
                       );

            }

        }
    }


    /*
    ################################################################################
    ############################# error handling  ##################################
    ################################################################################
     */

    /**
     * handleError
     *
     * handler for incorrect input parameters
     * e.g. if we expect int but String is given
     *
     * @param ex -
     * @return ResponseEntity -
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class})
    ResponseEntity<ResultResponse<ErrorDto>> handleError(
            HttpServletRequest req,
            Exception ex
    ) {

        return ResponseEntity.ok(new ResultResponseOperator<>(
                ResultResponseOperator.Codes.ERROR,
                String.format( "Incorrect input data [%s]", ex.getMessage())
        ));

    }

    /*
    ################################################################################
    ########################## controller methods ##################################
    ################################################################################
     */

    /**
     *
     * @param page -
     * @param size -
     * @return ResponseEntity -
     */
    @Logger(
            logTypes = Logger.TYPE.REQUEST
    )
    @RequestMapping(
            value = "all",
            method = {RequestMethod.GET, RequestMethod.POST}
    )
    public ResponseEntity<ResultResponse<OperatorDto>> getAllPaginated(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) {

        List<Operator> allOperators;

        if(page != null && size != null) {
            allOperators = operatorService.findPaginated(page, size);
        } else {
            allOperators = operatorService.findAll();
        }

        if (allOperators.size() == 0) {
            return ResponseEntity.ok(new ResultResponseOperator<>(
                    ResultResponseOperator.Codes.NOT_FOUND
            ));
        }

        List<OperatorDto> allDtos = ObjectUtils.convertPojoToDto(OperatorDto.class, allOperators);

        return ResponseEntity.ok(new ResultResponseOperator<>(allDtos));

    }

    /**
     * getAllGroupped
     *
     * @param groupBy -
     * @return ResponseEntity -
     */
    @Logger(
            logTypes = Logger.TYPE.REQUEST
    )
    @RequestMapping(
            value = "all-grouped",
            method = RequestMethod.GET,
            params = { "groupBy" }
    )
    public ResponseEntity<MapResponse<?, Set<OperatorDto>>> getAllGroupped(
            @RequestParam(value = "groupBy") String groupBy
    ) {

        if(!GroupBy.isAllowed(groupBy)){
            return ResponseEntity.ok(new MapResponse<>(
                    MapResponse.Codes.ERROR,
                    "groupBy parameter is not allowed"
            ));
        }

        List<Operator> allOperators = operatorService.findAll();

        if (allOperators.size() == 0) {
            return ResponseEntity.ok(new MapResponse<>(
                    MapResponse.Codes.NOT_FOUND
            ));
        }

        List<OperatorDto> allDtos = ObjectUtils.convertPojoToDto(OperatorDto.class, allOperators);

        // groupping data

        if(groupBy.equalsIgnoreCase(GroupBy.TypeName.enumval)) {

            Map<String, Set<OperatorDto>> grouppedOperators = CollectionUtils.collGroupper(
                    allDtos,
                    OperatorDto::getTypeName
            );

            return ResponseEntity.ok(new MapResponse<>(grouppedOperators));

        } else if (groupBy.equalsIgnoreCase(GroupBy.Owner.enumval)){

            Map<String, Set<OperatorDto>> grouppedOperators = CollectionUtils.collGroupper(
                    allDtos,
                    OperatorDto::getOwnerSlug
            );

            return ResponseEntity.ok(new MapResponse<>(grouppedOperators));

        } else{
            return ResponseEntity.ok(new MapResponse<>(
                    MapResponse.Codes.ERROR,
                    "General error"
            ));
        }

    }

    /**
     * getByVehiclesOwners
     *
     * @param vehicleOwner -
     * @return ResponseEntity -
     */
    @Logger(
            logTypes = Logger.TYPE.REQUEST
    )
    @RequestMapping(
            value = "by-vehicle-owner",
            method = RequestMethod.POST,
            produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE},
            consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE}
    )
    public ResponseEntity<ResultResponseSimple<?>> getByVehiclesOwners(
            @Valid @RequestBody OperatorByVehicleOwner vehicleOwner
    ) {

        var vehicleTypes = Set.of(vehicleOwner.vehicleTypes);
        var ownerSlugs = Set.of(vehicleOwner.ownerSlugs);

        if(vehicleTypes.isEmpty() || ownerSlugs.isEmpty()) {
            return ResponseEntity.ok(new ResultResponseSimple<>(
                    ResultResponseSimple.Codes.ERROR,
                    "vehicleTypes and ownerSlugs should not be empty"
            ));
        }

        Set<Vehicle> gVehicles = operatorService.getOperatorVehiclesByOwners(vehicleTypes, ownerSlugs);
        Set<Operator> gOperators = operatorService.getOperatorsWithVehiclesAndOwners(vehicleTypes, ownerSlugs);

        /*
        use FORK-JOIN pattern to compute result faster
        (split computation per vehicle and join partial results then)
         */
        ForkJoinPool fjPool = new ForkJoinPool();
        OperatorGroupperTask moObl = new OperatorGroupperTask(gVehicles, gOperators, ownerSlugs);
        Map<String, Map<String, Set<OperatorDto>>> vehiclesOperators = fjPool.invoke(moObl);

        return ResponseEntity.ok(new ResultResponseSimple<>(vehiclesOperators));

    }

    /**
     * getOperatorIdsByType
     *
     * @return ResponseEntity
     */
    @Logger(
            logTypes = Logger.TYPE.REQUEST
    )
    @RequestMapping(
            value = "id-by-type",
            method = RequestMethod.GET
    )
    public ResponseEntity<MapResponse<String, Set<Long>>> getOperatorIdsByType() {

        List<Operator> allOperators = operatorService.findAll();

        if (allOperators.size() == 0) {
            return ResponseEntity.ok(new MapResponse<>(
                    MapResponse.Codes.NOT_FOUND
            ));
        }

        List<OperatorDto> allDtos = ObjectUtils.convertPojoToDto(OperatorDto.class, allOperators);

        Map<String, Set<Long>> operatorIdsByType = CollectionUtils.collGroupperFlatter(
                allDtos,
                OperatorDto::getTypeName,
                OperatorDto::getId
        );

        return ResponseEntity.ok(new MapResponse<>(operatorIdsByType));

    }

    /**
     * findOne
     *
     * @param id -
     * @return ResponseEntity -
     */
    @Logger(
            logTypes = Logger.TYPE.REQUEST
    )
    @RequestMapping(
            value = "get/{id}",
            method = RequestMethod.GET
    )
    public ResponseEntity<ResultResponse<OperatorDto>> findOne(
            @PathVariable("id") Long id
    ){

        if (id <= 0) {
            return ResponseEntity.ok(new ResultResponseOperator<>(
                    ResultResponseOperator.Codes.ERROR,
                    "groupBy parameter is not allowed"
            ));
        }

        Operator operator = operatorService.findOneById(id);

        if (operator == null) {
            return ResponseEntity.ok(new ResultResponseOperator<>(
                    ResultResponseOperator.Codes.NOT_FOUND
            ));
        }

        OperatorDto operatorDto = ObjectUtils.convertPojoToDto(OperatorDto.class, operator);

        return ResponseEntity.ok(new ResultResponseOperator<>(operatorDto));

    }

    /**
     * findOneHead
     *
     * TODO
     *
     * @param id -
     * @return ResponseEntity
     */
    @RequestMapping(
            value = "get/{id}",
            method = RequestMethod.HEAD
    )
    public ResponseEntity<ResultResponse<OperatorDto>> findOneHead(
            @PathVariable("id") Long id
    ){
        return ResponseEntity.ok(new ResultResponseOperator<>());
    }

    /**
     * create
     *
     * @param operator -
     * @param result -
     * @return ResponseEntity
     */
    @Logger(
            logTypes = Logger.TYPE.REQUEST
    )
    @RequestMapping(
            value = "add",
            method = RequestMethod.PUT
    )
    public ResponseEntity<ResultResponse<OperatorDto>> create(
            @Valid @RequestBody OperatorDto operator,
            BindingResult result
    ) {

        try {

            parseValidationErrors(result);

            Operator newOperator = operatorService.create(operator);

            OperatorDto newOperatorDto = ObjectUtils.convertPojoToDto(OperatorDto.class, newOperator);

            // flush Redis cache event
            eventPublisher.publishEvent(new OnOperatorChangeFlushCacheEvent(this.getClass(), "add"));

            return ResponseEntity.ok(new ResultResponseOperator<>(newOperatorDto));

        } catch (ValidationException exc){

            ResultResponseOperator<OperatorDto> response = new ResultResponseOperator<>(
                    ResultResponse.Codes.ERROR,
                    exc.getMessage()
            );

            return ResponseEntity.ok(response);
        }

    }

    /**
     * update
     *
     * @param id -
     * @param operator -
     * @param result -
     * @return ResponseEntity
     */
    @Logger(
            logTypes = Logger.TYPE.REQUEST
    )
    @RequestMapping(
            value = "update/{id}",
            method = RequestMethod.PATCH
    )
    public ResponseEntity<ResultResponse<OperatorDto>> update(
            @PathVariable("id") Long id,
            @Valid @RequestBody OperatorDto operator,
            BindingResult result
    ) {

        try {

            Predicate<ObjectError> duplicatedSlug = o -> Objects
                    .requireNonNull(o.getCode())
                    .equals(ValidateOperatorCode.class.getSimpleName());

            parseValidationErrorsWithFilter(result, duplicatedSlug);

            // TODO - change ValidateOperatorCode to validate format but dont validate code repeat

            Operator newOperator = operatorService.updade(operator, id);

            OperatorDto newOperatorDto = ObjectUtils.convertPojoToDto(OperatorDto.class, newOperator);

            // flush Redis cache event
            eventPublisher.publishEvent(new OnOperatorChangeFlushCacheEvent(this.getClass(), "updt"));

            return ResponseEntity.ok(new ResultResponseOperator<>(newOperatorDto));

        } catch (ValidationException exc){

            ResultResponseOperator<OperatorDto> response = new ResultResponseOperator<>(
                    ResultResponse.Codes.ERROR,
                    exc.getMessage()
            );

            return ResponseEntity.ok(response);
        }

    }

    @Logger(
            logTypes = Logger.TYPE.REQUEST
    )
    @RequestMapping(
            value = "delete/{id}",
            method = RequestMethod.DELETE
    )
    public ResponseEntity<ResultResponse<OperatorDto>> delete(
            @PathVariable("id") Long id
    ) {

        try {

            operatorService.delete(id);

            // flush Redis cache event
            eventPublisher.publishEvent(new OnOperatorChangeFlushCacheEvent(this.getClass(), "del"));

            return ResponseEntity.ok(new ResultResponseOperator<>());

        } catch (ValidationException exc){

            ResultResponseOperator<OperatorDto> response = new ResultResponseOperator<>(
                    ResultResponse.Codes.ERROR,
                    exc.getMessage()
            );

            return ResponseEntity.ok(response);
        }
    }

    /**
     * apiOptions
     *
     * TODO
     *
     * @return  ResponseEntity<String>
     */
    @RequestMapping(
            value = "options",
            method = RequestMethod.OPTIONS
    )
    public ResponseEntity<MapResponse<?, ?>> apiOptions(){

        ApiInfo api = SwaggerConfig.apiEndPointsInfo();
        Map<String, String> options = new HashMap<>();
        options.put("title", api.getTitle());
        options.put("description", api.getDescription());
        options.put("version", api.getVersion());
        options.put("contact", api.getContact().getEmail());

        return ResponseEntity.ok(new MapResponse<>(options));

    }

}