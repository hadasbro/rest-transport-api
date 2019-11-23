package github.hadasbro.transport.components;

import github.hadasbro.transport.database.ActionHandler;
import github.hadasbro.transport.domain.location.Point;
import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.domain.journey.Action;
import github.hadasbro.transport.domain.journey.Journey;
import github.hadasbro.transport.domain.journey.Journeyleg;
import github.hadasbro.transport.exceptions.ApiException;
import github.hadasbro.transport.repository.CityRepository;
import github.hadasbro.transport.repository.PassengerRepository;
import github.hadasbro.transport.services.OperatorService;
import github.hadasbro.transport.services.TransportService;
import github.hadasbro.transport.services.PassengerService;
import github.hadasbro.transport.utils.CollectionUtils;
import github.hadasbro.transport.utils.Utils;
import github.hadasbro.transport.webDto.ApiRequestDto;
import lombok.extern.java.Log;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Predicate;

import static github.hadasbro.transport.utils.ThreadUtils.excToCompletableExc;

/**
 * ProcessComponent
 *
 * handle all operations in api
 */
@Log
@Component
@Scope(value = "singleton")
@SuppressWarnings({"unused", "WeakerAccess"})
public class ProcessComponent {

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private TransportService transportService;

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    @Qualifier("dataSource1")
    DataSource dataSource;

    @Autowired
    ActionHandler actionHandler;

    @Autowired
    @Qualifier("entityManager")
    EntityManager entityManager;


    public ExecutorService executor = Executors.newFixedThreadPool(5);

    /**
     * checkJourneylegAndAction
     *
     * @param request -
     * @return Journeyleg
     * @throws ApiException -
     */
    public Journeyleg checkJourneylegAndAction(ApiRequestDto request, ApiException.CODES[] dontCheck) throws ApiException {

        /*
        all entity dependencies used here have fetch type = eager to avoid possible
        non thread access hibernate to journey or load all dependencies here if needed
         */

        List<Journeyleg> jlegActions = transportService.getJourneylegOrActions(
                request.getJourneylegIdentifer(),
                request.getActionIdentifier()
        );


        CompletableFuture<ApiException> actionAndJlegIsValid = CompletableFuture
                .supplyAsync(() -> true)
                .thenCompose(bl ->
                        CompletableFuture.allOf(

                                CompletableFuture.supplyAsync(() -> {

                                    if(Utils.arrayInclude(dontCheck, ApiException.CODES.JOURNEYL_NOT_FOUND)) {
                                        return null;
                                    }

                                    boolean jlegExists = jlegActions
                                            .stream().anyMatch(rd -> rd.getIdentifer().equals(request.getJourneylegIdentifer()));

                                    if(!jlegExists){
                                        throw excToCompletableExc(new ApiException(ApiException.CODES.JOURNEYL_NOT_FOUND));
                                    }

                                    return null;
                                }),

                                CompletableFuture.supplyAsync(() -> {

                                    if(Utils.arrayInclude(dontCheck, ApiException.CODES.ACTION_DUPLICATED)) {
                                        return null;
                                    }

                                    boolean duplicatedTrans = jlegActions
                                            .stream()
                                            .anyMatch(
                                                    rd -> rd
                                                            .getActions()
                                                            .stream()
                                                            .anyMatch(
                                                                    tr -> tr.getIdentifier()
                                                                            .equals(request.getActionIdentifier())
                                                            )
                                            );

                                    if(duplicatedTrans){
                                        throw excToCompletableExc(new ApiException(ApiException.CODES.ACTION_DUPLICATED));
                                    }

                                    return null;

                                }),

                                CompletableFuture.supplyAsync(() -> {

                                    if(Utils.arrayInclude(dontCheck, ApiException.CODES.JOURNEYL_CLOSED)) {
                                        return null;
                                    }

                                    boolean closed = jlegActions
                                            .stream()
                                            .filter(rd -> rd.getIdentifer().equals(request.getJourneylegIdentifer()))
                                            .anyMatch(
                                                    Journeyleg::isClosed
                                            );

                                    if(closed){
                                        throw excToCompletableExc(new ApiException(ApiException.CODES.JOURNEYL_CLOSED));
                                    }

                                    return null;

                                }),

                                CompletableFuture.supplyAsync(() -> {

                                    if(Utils.arrayInclude(dontCheck, ApiException.CODES.JOURNEYL_CANCELLED)) {
                                        return null;
                                    }

                                    boolean cancelled = jlegActions
                                            .stream()
                                            .filter(rd -> rd.getIdentifer().equals(request.getJourneylegIdentifer()))
                                            .anyMatch(
                                                    rd -> rd.getActions()
                                                            .stream()
                                                            .anyMatch(tr -> tr.getType() == Action.TYPE.REFUND)
                                            );

                                    if (cancelled) {
                                        throw excToCompletableExc(new ApiException(ApiException.CODES.JOURNEYL_CANCELLED));
                                    }

                                    return null;

                                })
                        )

                        .handle((res, ex) -> {

                            if (ex != null && ex.getCause() instanceof ApiException) {
                                return (ApiException) ex.getCause();
                            }
                            return null;

                        })
                );


        ApiException err = actionAndJlegIsValid.join();

        if (err != null) {
            throw err;
        }

        return jlegActions
                .stream()
                .filter(
                        rd -> rd.getIdentifer().equals(request.getJourneylegIdentifer())
                )
                .limit(1)
                .filter(Predicate.not(Objects::isNull))
                .collect(CollectionUtils.toSingleton());
    }

    /**
     * getPoint
     *
     * @param pointId -
     * @return Point -
     * @throws ApiException -
     */
    public Point getPoint(Long pointId) throws ApiException {
        return transportService
                .getPointById(pointId)
                .orElseThrow(() -> new ApiException(ApiException.CODES.POINT_DOESNT_EXIST));
    }

    /**
     * checkOperatorPassenger
     *
     * @param request -
     * @return -
     * @throws ApiException -
     */
    public MutablePair<Operator, Passenger> checkOperatorPassenger(ApiRequestDto request, ApiException.CODES[] dontCheck) throws ApiException {

        //Operators

        CompletableFuture<Operator> futureOperator =
                CompletableFuture

                        .supplyAsync(() -> {

                            // load operator

                            return operatorService
                                    .findFirstByLicenceIdAndType(request.getLicenceId(), request.getType())
                                    .orElseThrow(
                                            () -> excToCompletableExc(new ApiException(ApiException.CODES.OPER_NOT_FOUND))
                                    );

                        }, executor)

                        .thenApply(

                                // check restrictions

                                operator -> {

                                    try {

                                        if(Utils.arrayInclude(dontCheck, ApiException.CODES.OPER_RESTRICTED)) {
                                            return operator;
                                        }

                                        operatorService.checkOperatorRestrictions(operator);

                                    } catch (ApiException t) {
                                        throw excToCompletableExc(t);
                                    } catch (Throwable t) {
                                        throw excToCompletableExc(new ApiException(ApiException.CODES.GENERAL, t));
                                    }

                                    return operator;
                                }
                        );

        // passenger

        CompletableFuture<Passenger> futurePassenger = CompletableFuture

                .supplyAsync(() -> {

                    // load passenger

                    return passengerService
                            .findById(request.getPassengerId())
                            .orElseThrow(
                                    () -> excToCompletableExc(new ApiException(ApiException.CODES.PASSENGER_NOT_FOUND))
                            );


                }, executor)


                .thenApply(

                        // check restrictions

                        passenger -> {

                            try {

                                if(
                                        Utils.arrayInclude(dontCheck, ApiException.CODES.PASSENGER_NOACTIVE)
                                        && Utils.arrayInclude(dontCheck, ApiException.CODES.PASSENGER_BLOCKED)
                                ) {
                                    return passenger;
                                }
                                passengerService.checkPassengerRestrictions(passenger);
                                passengerService.checkPassengerLimits(passenger);

                            } catch (ApiException t) {
                                throw excToCompletableExc(t);
                            } catch (Throwable t) {
                                throw excToCompletableExc(new ApiException(ApiException.CODES.GENERAL, t));
                            }

                            return passenger;
                        }
                );


        // check all (operator, journey, passenger)

        CompletableFuture<ApiException> operatorJourneyPassenger = CompletableFuture

                .allOf(futureOperator, futurePassenger)

                .handle((res, ex) -> {

                    if (ex != null && ex.getCause() instanceof ApiException) {
                        return (ApiException) ex.getCause();
                    } else if (ex != null) {
                        log.warning(ex.toString());
                    }

                    return null;

                });


        // wait & join here and check if there is no any blocker
        ApiException gspBlockers = operatorJourneyPassenger.join();

        if (gspBlockers != null) {
            throw gspBlockers;
        }

        // if no errors then <futureOperator, futurePassenger> should be comleted here

        Operator operator = futureOperator.join();
        Passenger passenger = futurePassenger.join();

        // check if all good with operator + passenger

        CompletableFuture<ApiException> passengerForOperatorIsValid =
                CompletableFuture
                        .supplyAsync(() -> {

                            try {
                                //load passenger balance for operator
                                if(Utils.arrayInclude(dontCheck, ApiException.CODES.PASSENGER_LINE)) {
                                    return null;
                                }
                                operatorService.checkPassengerCanUseOperatorsLine(operator, passenger);
                            } catch (ApiException t) {
                                return t;
                            } catch (Throwable t) {
                                throw excToCompletableExc(new ApiException(ApiException.CODES.GENERAL, t));
                            }
                            return null;
                        });

        // check all (passenger + operator)

        List<CompletableFuture<ApiException>> allCompletables = new ArrayList<>() {{
            add(passengerForOperatorIsValid);
        }};

        Optional<ApiException> apiException = allCompletables
                .stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .limit(1)
                .findFirst();


        if(apiException.isPresent()) {
            throw apiException.get();
        }

        return new MutablePair<>(operator, passenger);

    }

    /**
     * Check request's Operator, Journey and Passenger
     * @param request -
     * @return MutableTriple<Operator, Journey, Passenger>
     * @throws ApiException -
     */
    public MutableTriple<Operator, Journey, Passenger> checkJourneyOperatorPassenger(ApiRequestDto request) throws ApiException {

        // journey

        CompletableFuture<Journey> futureJourney = CompletableFuture

                .supplyAsync(() -> {

                    // load journey

                    return transportService
                            .getJourneyByIdentifer(request.getJourneyIdentifer())
                            .orElseThrow(
                                    () -> excToCompletableExc(new ApiException(ApiException.CODES.JOURNEY_NOT_FOUND))
                            );

                }, executor)

                .thenApply(

                        // check restrictions

                        journey -> {

                            try {
                                transportService.checkJourneyRestrictions(journey);
                            } catch (ApiException t) {
                                throw excToCompletableExc(t);
                            } catch (Throwable t) {
                                throw excToCompletableExc(new ApiException(ApiException.CODES.GENERAL, t));
                            }

                            return journey;
                        }
                );


        //Operators

        CompletableFuture<Operator> futureOperator =
                CompletableFuture

                        .supplyAsync(() -> {

                            // load operator

                            return operatorService
                                    .findFirstByLicenceIdAndType(request.getLicenceId(), request.getType())
                                    .orElseThrow(
                                            () -> excToCompletableExc(new ApiException(ApiException.CODES.OPER_NOT_FOUND))
                                    );

                        }, executor)

                        .thenApply(

                                // check restrictions

                                operator -> {

                                    try {

                                        operatorService.checkOperatorRestrictions(operator);

                                    } catch (ApiException t) {
                                        throw excToCompletableExc(t);
                                    } catch (Throwable t) {
                                        throw excToCompletableExc(new ApiException(ApiException.CODES.GENERAL, t));
                                    }

                                    return operator;
                                }
                        );

        // passenger

        CompletableFuture<Passenger> futurePassenger = CompletableFuture

                .supplyAsync(() -> {

                    // load passenger

                    return passengerService
                            .findById(request.getPassengerId())
                            .orElseThrow(
                                    () -> excToCompletableExc(new ApiException(ApiException.CODES.PASSENGER_NOT_FOUND))
                            );


                }, executor)


                .thenApply(

                        // check restrictions

                        passenger -> {

                            try {

                                passengerService.checkPassengerRestrictions(passenger);
                                passengerService.checkPassengerLimits(passenger);

                            } catch (ApiException t) {
                                throw excToCompletableExc(t);
                            } catch (Throwable t) {
                                throw excToCompletableExc(new ApiException(ApiException.CODES.GENERAL, t));
                            }

                            return passenger;
                        }
                );


        // check all (operator, journey, passenger)

        CompletableFuture<ApiException> operatorJourneyPassenger = CompletableFuture

                .allOf(futureJourney, futureOperator, futurePassenger)

                .handle((res, ex) -> {

                    if (ex != null && ex.getCause() instanceof ApiException) {
                        return (ApiException) ex.getCause();
                    } else if (ex != null) {
                        log.warning(ex.toString());
                    }

                    return null;

                });


        // wait & join here and check if there is no any blocker
        ApiException gspBlockers = operatorJourneyPassenger.join();

        if (gspBlockers != null) {
            throw gspBlockers;
        }

        // if no errors then <futureJourney, futureOperator, futurePassenger> should be comleted here

        Operator operator = futureOperator.join();
        Journey journey = futureJourney.join();
        Passenger passenger = futurePassenger.join();

        // check if all good with operator + journey, journey + passenger, operator + passenger

        CompletableFuture<ApiException> passengerForJourneyIsValid =
                CompletableFuture
                        .supplyAsync(() -> {

                            try {

                                transportService.checkJourneyForPassenger(journey, passenger);
                                transportService.checkJourneyLimits(journey, passenger);

                            } catch (ApiException t) {
                                return t;
                            } catch (Throwable t) {
                                throw excToCompletableExc(new ApiException(ApiException.CODES.GENERAL, t));
                            }
                            return null;
                        });


        CompletableFuture<ApiException> passengerForOperatorIsValid =
                CompletableFuture
                        .supplyAsync(() -> {

                            try {
                                //load passenger balance for operator
                                operatorService.checkPassengerCanUseOperatorsLine(operator, passenger);
                            } catch (ApiException t) {
                                return t;
                            } catch (Throwable t) {
                                throw excToCompletableExc(new ApiException(ApiException.CODES.GENERAL, t));
                            }
                            return null;
                        });


        CompletableFuture<ApiException> operatorJourneyIsValid =
                CompletableFuture
                        .supplyAsync(() -> {

                            try {
                                transportService.checkJourneyForOperator(journey, operator);
                            } catch (ApiException t) {
                                return t;
                            } catch (Throwable t) {
                                throw excToCompletableExc(new ApiException(ApiException.CODES.GENERAL, t));
                            }
                            return null;
                        });


        // check all (passenger + operator + journey)

        List<CompletableFuture<ApiException>> allCompletables = new ArrayList<>() {{
            add(passengerForJourneyIsValid);
            add(passengerForOperatorIsValid);
            add(operatorJourneyIsValid);
        }};

        Optional<ApiException> apiException = allCompletables
                .stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .limit(1)
                .findFirst();


        if(apiException.isPresent()) {
            throw apiException.get();
        }

        return new MutableTriple<>(operator, journey, passenger);

    }

    /**
     * handleTransportAction
     * touchin touchout refund
     *
     * @param passenger -
     * @param action -
     * @param journeyleg -
     */
    @org.springframework.transaction.annotation.Transactional(value = "transactionManager", rollbackFor = ApiException.class)
    public void handleAction(Passenger passenger, Action action, Journeyleg journeyleg, Journey journey) throws ApiException {

        // run stored procedure

        ActionHandler actionHandler = new ActionHandler(entityManager);

        actionHandler.execute(passenger, action);

        this.entityManager.merge(passenger);

        transportService.addJourneyleg(journeyleg);

        transportService.addAction(action);

    }
    @org.springframework.transaction.annotation.Transactional(value = "transactionManager", rollbackFor = ApiException.class)
    public void handleInitAction(Passenger passenger, Action action, Journey journey) {

        this.entityManager.merge(passenger);

        transportService.addJourney(journey);

        transportService.addAction(action);

    }
}
