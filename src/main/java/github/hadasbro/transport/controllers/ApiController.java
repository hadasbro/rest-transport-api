package github.hadasbro.transport.controllers;

import github.hadasbro.transport.aspects.Logger;
import github.hadasbro.transport.components.ProcessComponent;
import github.hadasbro.transport.domain.journey.Action;
import github.hadasbro.transport.domain.journey.Action.TYPE;
import github.hadasbro.transport.domain.journey.Journey;
import github.hadasbro.transport.domain.journey.Journeyleg;
import github.hadasbro.transport.domain.location.Point;
import github.hadasbro.transport.domain.logger.ApiLogger;
import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.exceptions.ApiException;
import github.hadasbro.transport.repository.CityRepository;
import github.hadasbro.transport.services.OperatorService;
import github.hadasbro.transport.services.PassengerService;
import github.hadasbro.transport.services.TransportService;
import github.hadasbro.transport.validators.api.ApiValidators;
import github.hadasbro.transport.webDto.ApiRequestDto;
import github.hadasbro.transport.webDto.ApiResponseDto;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static github.hadasbro.transport.exceptions.ApiException.CODES;
import static github.hadasbro.transport.exceptions.ApiException.CODES.*;
import static github.hadasbro.transport.utils.CollectionUtils.getFirstFromCollection;
import static github.hadasbro.transport.webDto.ApiRequestDto.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_XML_VALUE;


@SuppressWarnings({"unused", "WeakerAccess"})
@Log
@RestController
@RequestMapping(
        value = "/api/",
        produces = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE},
        consumes = {APPLICATION_JSON_VALUE, APPLICATION_XML_VALUE}
)
/* there is no CORS here as this is a server-server communication*/
class ApiController extends BaseController {

    public ExecutorService executor = Executors.newFixedThreadPool(5);

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private ProcessComponent processComponent;

    @Autowired
    private TransportService transportService;

    @Autowired
    private CityRepository cityRepository;



    /*
    ################################################################################
    ####################### error/exception handlers ###############################
    ################################################################################
     */

    /**
     * handleInvalidJsonError
     *
     * handler for incorrect input parameters
     * e.g. if we expect int but String is given
     *
     * @param ex -
     * @return ResponseEntity -
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    ResponseEntity<ApiResponseDto> handleInvalidJsonError(
            HttpServletRequest req,
            Exception ex
    ) {

        // invalid json
        return ResponseEntity.ok(new ApiResponseDto(INCORRECT_DATA_FORMAT));

    }

    /*
    ################################################################################
    ########################## controller methods ##################################
    ################################################################################
     */

    /**
     * init journey
     *
     * @param request -
     * @param result -
     * @return ResponseEntity -
     */
    @Logger(
            clazz = ApiLogger.class
    )
    @RequestMapping(
            value = URI_INIT_JOURNEY,
            method = RequestMethod.POST
    )
    public ResponseEntity<ApiResponseDto> init(
            @Validated({
                    ApiValidators.ApiInitGroup.class,
                    ApiValidators.ApiGeneralGroup.class
            })
            @RequestBody ApiRequestDto request,
            BindingResult result,
            HttpServletRequest httpRequest
    ) {

        try {

            // validate input data
            parseValidationErrors(result);

            // check point exist
            Point point = processComponent.checkPoint(request.getPointId());

            // validate operator and passenger
            MutablePair<Operator, Passenger> gsp = processComponent.checkOperatorPassenger(request);

            Operator operator = gsp.left;
            Passenger passenger = gsp.right;

            CompletableFuture.runAsync(
                    () -> {
                        Action action = new Action();
                        action.setType(TYPE.INIT_JOURNEY);
                        action.setIdentifier(request.getActionIdentifier());
                        action.setPoint(point);
                        transportService.addAction(action);
                    }, executor
            );

            CompletableFuture.runAsync(
                    () -> {
                        Journey newJourney = new Journey();
                        newJourney.setOperator(operator);
                        newJourney.setPassenger(passenger);
                        newJourney.setIdentifer(request.getJourneylegIdentifer());
                        transportService.createJourney(newJourney);
                    }, executor
            );


            return ResponseEntity.ok(new ApiResponseDto(passenger));

        } catch (ApiException wex) {
            return ResponseEntity.ok(new ApiResponseDto(wex));
        } catch (Throwable t){
            log.warning(t.toString());
            return ResponseEntity.ok(new ApiResponseDto(GENERAL));
        }

    }

    /**
     * balance
     *
     * @param request -
     * @param result -
     * @return ResponseEntity -
     */
    @Logger(
            clazz = ApiLogger.class
    )
    @RequestMapping(
            value = URI_BALANCE,
            method = RequestMethod.POST
    )
    public ResponseEntity<ApiResponseDto> balance(
            @Validated({
                    ApiValidators.ApiGeneralGroup.class,
            })
            @RequestBody ApiRequestDto request,
            BindingResult result,
            HttpServletRequest httpRequest
    ) {

        try {

            parseValidationErrors(result);

            // validate operator and passenger
            MutablePair<Operator, Passenger> gsp = processComponent.checkOperatorPassenger(request);

            Operator operator = gsp.left;
            Passenger passenger = gsp.right;

            CompletableFuture.runAsync(
                    () -> transportService.refreshJourneyDataWebsocket(passenger), executor
            );

            return ResponseEntity.ok(new ApiResponseDto(gsp.right));

        } catch (ApiException wex) {
            return ResponseEntity.ok(new ApiResponseDto(wex));
        } catch (Throwable t){
            log.warning(t.toString());
            return ResponseEntity.ok(new ApiResponseDto(GENERAL));
        }

    }

    /**
     * touchin
     *
     * @param request -
     * @param result -
     * @return ResponseEntity -
     */
    @Logger(
            clazz = ApiLogger.class
    )
    @RequestMapping(
            value = {URI_TOUCHIN},
            method = RequestMethod.POST
    )
    public ResponseEntity<ApiResponseDto> touchin(
            @Validated({
                    ApiValidators.ApiActionGroup.class,
                    ApiValidators.ApiGeneralGroup.class
            })
            @RequestBody ApiRequestDto request,
            BindingResult result,
            HttpServletRequest httpRequest
    ) {

        try {

            parseValidationErrors(result);

            CODES[] dontCheck = new CODES[]{JOURNEYL_NOT_FOUND};

            int amountCents = 0; //request.getAmount() / ApiRequestDto.CENTS;

            Double amount = Math.abs(Double.valueOf(amountCents));

            // validate operator, journey and passenger
            MutableTriple<Operator, Journey, Passenger> gsp = processComponent.checkOperatorJourneyPassenger(request);

            Operator operator = gsp.left;
            Journey journey = gsp.middle;
            Passenger passenger = gsp.right;

            // check if passenger has enough money
            if(!passengerService.passengerHasMoney(passenger, amount)){
                throw new ApiException(INSUFFICIENT_FUNDS);
            }

            // validate journeyleg
            Journeyleg journeyleg = processComponent.checkJourneylegAndAction(request, dontCheck);

            // first touchin in the journeyleg, create new journeyleg
            if(journeyleg == null) {

                journeyleg = new Journeyleg();
//                journeyleg.ad(journey);
                journeyleg.setIdentifer(request.getJourneylegIdentifer());
                journeyleg.setPassenger(passenger);
                journeyleg.setOperator(operator);
                journeyleg.setClosed(false);

            }

            Action action = new Action();
            action.setIdentifier(request.getActionIdentifier());
            action.setType(TYPE.TOUCH_IN);
//            action.se(amount);

            // add journeyleg + action dependencies
            journeyleg.addAction(action);
//            action.setJourneyleg(journeyleg);


            // handle
            processComponent.handleAction(passenger, action, journeyleg, journey);


            return ResponseEntity.ok(new ApiResponseDto(passenger));

        } catch (ApiException wex) {
            return ResponseEntity.ok(new ApiResponseDto(wex));
        } catch (Throwable t){
            log.warning(t.toString());
            return ResponseEntity.ok(new ApiResponseDto(GENERAL));
        }

    }

    /**
     * touchout
     *
     * @param request -
     * @param result -
     * @return ResponseEntity -
     */
    @Logger(
            clazz = ApiLogger.class
    )
    @RequestMapping(
            value = {URI_TOUCHOUT},
            method = RequestMethod.POST
    )
    public ResponseEntity<ApiResponseDto> touchout(
            @Validated({
                    ApiValidators.ApiActionGroup.class,
                    ApiValidators.ApiGeneralGroup.class
            })
            @RequestBody ApiRequestDto request,
            BindingResult result,
            HttpServletRequest httpRequest
    ) {

        try {

            parseValidationErrors(result);

            CODES[] dontCheck = new CODES[0];

            int amountCents = 0;//request.getAmount() / ApiRequestDto.CENTS;

            Double amount = Math.abs(Double.valueOf(amountCents));

            // validate operator, journey and passenger
            MutableTriple<Operator, Journey, Passenger> gsp = processComponent.checkOperatorJourneyPassenger(request);

            Operator operator = gsp.left;
            Journey journey = gsp.middle;
            Passenger passenger = gsp.right;

            // validate journeyleg
            Journeyleg journeyleg = processComponent.checkJourneylegAndAction(request, dontCheck);

            // first touchin action in the journeyleg, create new journeyleg
            if(journeyleg == null) {
                throw new ApiException(JOURNEYL_NOT_FOUND);
            }

            Action action = new Action();
            action.setIdentifier(request.getActionIdentifier());
            action.setType(TYPE.TOUCH_OUT);
//            action.setAmount(amount);

            // add journeyleg + action dependencies
            journeyleg.addAction(action);
//            action.setJourneyleg(journeyleg);

            // handle
            processComponent.handleAction(passenger, action, journeyleg, journey);

            return ResponseEntity.ok(new ApiResponseDto(passenger));

        } catch (ApiException wex) {
            return ResponseEntity.ok(new ApiResponseDto(wex));
        } catch (Throwable t){
            log.warning(t.toString());
            return ResponseEntity.ok(new ApiResponseDto(GENERAL));
        }

    }

    /**
     * Refund
     *
     * @param request -
     * @param result -
     * @return ResponseEntity -
     */
    @Logger(
            clazz = ApiLogger.class
    )
    @RequestMapping(
            value = {URI_REFUND},
            method = RequestMethod.POST
    )
    public ResponseEntity<ApiResponseDto> refund(
            @Validated({
                    ApiValidators.ApiActionGroup.class,
                    ApiValidators.ApiGeneralGroup.class
            })
            @RequestBody ApiRequestDto request,
            BindingResult result,
            HttpServletRequest httpRequest
    ) {

        try {

            parseValidationErrors(result);

            CODES[] dontCheck = new CODES[0];

            int amountCents = 0; //request.getAmount() / ApiRequestDto.CENTS;

            Double amount = Math.abs(Double.valueOf(amountCents));

            Journeyleg journeyleg = transportService.getJourneylegByActions(request.getActionIdentifier(), TYPE.TOUCH_IN);

            // no journeyleg to refund
            if(journeyleg == null) {
                throw new ApiException(JOURNEYL_NOT_FOUND);
            }

            Journey journey = journeyleg.getJourney();

            Action actionTouchIn = getFirstFromCollection(journeyleg.getActions());

            Action action = new Action();
            action.setIdentifier(request.getActionIdentifier());
            action.setType(TYPE.INIT_JOURNEY);
//            action.setAmount(amount);

//            if(!actionTouchIn.getAmount().equals(action.getAmount())) {
//                throw new ApiException(AMOUNT_DOESNT_MATCH);
//            }

            Passenger passenger = journeyleg.getPassenger();

            // handle
            processComponent.handleAction(passenger, action, journeyleg, journey);

            return ResponseEntity.ok(new ApiResponseDto(passenger));

        } catch (ApiException wex) {
            return ResponseEntity.ok(new ApiResponseDto(wex));
        } catch (Throwable t){
            log.warning(t.toString());
            return ResponseEntity.ok(new ApiResponseDto(GENERAL));
        }

    }
}