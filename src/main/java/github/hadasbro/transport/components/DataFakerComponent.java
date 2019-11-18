package github.hadasbro.transport.components;

import github.hadasbro.transport.domain.location.Country;
import github.hadasbro.transport.domain.location.Point;
import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.domain.transport.Owner;
import github.hadasbro.transport.domain.location.City;
import github.hadasbro.transport.domain.transport.Vehicle;
import github.hadasbro.transport.domain.journey.Journey;
import github.hadasbro.transport.domain.journey.Journeyleg;
import github.hadasbro.transport.domain.journey.Action;
import github.hadasbro.transport.domain.passenger.Accesscard;
import github.hadasbro.transport.services.OperatorService;
import github.hadasbro.transport.services.TransportService;
import github.hadasbro.transport.services.GeneralService;
import github.hadasbro.transport.services.PassengerService;
import github.hadasbro.transport.utils.CollectionUtils;
import github.hadasbro.transport.utils.Utils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;


@Profile({"prod", "dev", "docker"})
@Component
@Scope(value = "singleton")
@SuppressWarnings({"unused"})
public class DataFakerComponent {

    // 0 - no run, 1 - run with truncate, 2 - just run
    @Value("${data.faker:0}")
    private int mode;

    private ExecutorService executor;

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private GeneralService generalService;

    @Autowired
    private OperatorService operatorService;

    @Autowired
    private PassengerService passengerService;

    @Autowired
    private TransportService transportService;


    DataFakerComponent() {}

    @PostConstruct
    public void pConstruct() {
        executor = Executors.newFixedThreadPool(5);
    }

    /**
     * loadFakeData
     */
    @Transactional
    public void loadFakeData() {

        if (mode == 0) {
            return;
        }

        final ArrayList<Operator> operatorList = new ArrayList<>();
        final List<Journeyleg> journeylegList = new ArrayList<>();
        final List<Action> actionList  = new ArrayList<>();
        final List<Passenger> passengerList  = new ArrayList<>();
        final List<Point> points  = new ArrayList<>();


        // generateOwners
        CompletableFuture<ArrayList<Owner>> ownersList = CompletableFuture.supplyAsync(this::generateOwners, executor);

        // generateVehicles();
        CompletableFuture<Set<Vehicle>> vehiclesSet = CompletableFuture.supplyAsync(this::generateVehicles, executor);

        // generateCitiesAndCountries()
        CompletableFuture<Set<City>> citiesList = CompletableFuture.supplyAsync(this::generateCitiesAndCountries, executor);

        // generatePoints()
        CompletableFuture<Set<Point>> pointsList = CompletableFuture.supplyAsync(this::generatePoints, executor);

        CompletableFuture.runAsync(() -> {

            if (mode == 1) {

                // truncate data

                generalService.truncateTables(new Class<?>[]{
                        Operator.class,
                        Vehicle.class,
                        Owner.class,
                        Journeyleg.class,
                        Action.class,
                        Journey.class,
                        Passenger.class
                });

            }

        })

        .thenRun(
                () -> CompletableFuture.allOf(ownersList, citiesList, vehiclesSet, pointsList)

                    .thenApply(
                            dm -> CompletableFuture.supplyAsync(
                                    () -> {
                                         passengerList.addAll(generateUsers(citiesList.join()));
                                         return passengerList;
                                    },
                                    executor)
                    )

                    .thenCompose(
                            dm -> CompletableFuture.supplyAsync(
                                    () -> generateOperators(ownersList.join(), vehiclesSet.join(), citiesList.join()),
                                    executor)
                    )

                    .thenCompose(
                            operators -> CompletableFuture.supplyAsync(() -> {
                                operatorList.addAll(operators);
                                return generateJourneys(operators, passengerList);
                            }, executor)
                    )

                    .thenCompose(
                            journeys -> CompletableFuture.supplyAsync(
                                    () -> generateJourneyleg(operatorList, passengerList, journeys), executor)
                    )

                    .thenCompose(
                            journeylegs -> CompletableFuture.supplyAsync(
                                    () -> generateActions(journeylegs, pointsList.join()), executor)
                    )

        );

    }

    /**
     * generateVehicles
     *
     * @return ArrayList<Owner>
     */
    private ArrayList<Owner> generateOwners() {

        return operatorService.addOperatorOwners(
                CollectionUtils.objGenerateSet(20, i -> {
                            Owner owner = new Owner();
                            owner.setName("Owner " + i);
                            owner.setSlug("owner-slug-" + i);
                            return owner;
                        }
                ));
    }

    /**
     * generateCitiesAndCountries
     *
     * @return ArrayList<City>
     */
    private Set<City> generateCitiesAndCountries() {

        Set<Country> countries = generalService.addCountries(
                CollectionUtils.objGenerateSet(20,
                        i -> {
                            Country c = new Country();
                            c.setIsoCode("CTR" + i);
                            c.setName("Country " + i);
                            return c;
                        }
                ));

        return generalService.addCities(
            CollectionUtils.objGenerateSet(20,
                    i -> {
                        City gc = new City();
                        gc.setCityCode("CI" + i);
                        gc.setName("City " + i);
                        gc.setCountry(CollectionUtils.getRandomElement(new ArrayList<>(countries), i));
                        return gc;
                    }
            ));

    }

    /**
     * generatePoints
     *
     * @return List<Point> -
     */
    private Set<Point> generatePoints() {
        return transportService.addPoints(
                CollectionUtils.objGenerateSet(20,
                        i -> new Point(null, "point " + i)
                ));
    }

    /**
     * generateVehicles
     *
     * @return Set<Vehicle>
     */
    private Set<Vehicle> generateVehicles() {

        return operatorService.addOperatorVehicles(
                CollectionUtils.objGenerateSet(5,

                        i -> {

                            Vehicle.TYPE type;

                            switch (i) {
                                case 1:
                                    type = Vehicle.TYPE.UNDERGROUND;
                                    break;
                                case 2:
                                    type = Vehicle.TYPE.OVERGROUND;
                                    break;
                                case 3:
                                    type = Vehicle.TYPE.CABLECAR;
                                    break;
                                case 4:
                                    type = Vehicle.TYPE.DLR;
                                    break;
                                case 0:
                                default:
                                    type = Vehicle.TYPE.TRAIN;
                                    break;
                            }

                            return new Vehicle(
                                    null,
                                    StringUtils.capitalize(type.name().toLowerCase()),
                                    type.name().toLowerCase(),
                                    type,
                                    null
                            );
                        }
                ));
    }

    /**
     * generateOperators
     *
     * @param owners -
     * @param vehicles -
     * @return List<Operator> -
     */
    private List<Operator> generateOperators(ArrayList<Owner> owners, Set<Vehicle> vehicles, Set<City> cities) {

        return operatorService.add(CollectionUtils.objGenerateSet(20,
                i -> {

                    Owner owner = CollectionUtils.getRandomElement(owners, i);

                    int type;

                    switch (Utils.rand(0, 3)) {
                        case 0:
                            type = Operator.TYPE_PRIVATE;
                            break;
                        case 1:
                            type = Operator.TYPE_MIXED;
                            break;
                        case 2:
                        default:
                            type = Operator.TYPE_NATIONAL;
                            break;
                    }

                    int status;

                    switch (Utils.rand(0, 3)) {
                        case 0:
                            status = Operator.STATUS_ACTIVE;
                            break;
                        case 1:
                            status = Operator.STATUS_INOPERATION;
                            break;
                        case 2:
                        default:
                            status = Operator.STATUS_INACTIVE;
                            break;
                    }

                    Operator operator = new Operator();
                    operator.setName("Operator #" + i);
                    operator.setOwner(owner);
                    operator.setOperatorCode("operator-errorCode-" + i);
                    operator.setLicenceId(5000 + i);
                    operator.setType(type);
                    operator.setStatus(status);
                    operator.setVehicles(
                        vehicles
                            .stream()
                            // filter a bit random values
                            .filter((el) -> Utils.rand(0, 3) == 0)
                            .collect(Collectors.toSet())
                    );
                    operator.setCities(
                            new HashSet<>(cities)
                    );
                    return operator;
                }));
    }

    /**
     * generateUsers
     *
     * @return List<Passenger> -
     */
    private List<Passenger> generateUsers(Set<City> cities) {

        return passengerService.add(
                CollectionUtils.objGenerateSet(50, i -> {

                    City city = CollectionUtils.getRandomElement(new ArrayList<>(cities), i);

                    Passenger passenger = new Passenger();
                    passenger.setActive(true);
                    passenger.setBlocked(false);
                    passenger.setFirstName("John" + i);
                    passenger.setLastName("Doe" + i);
                    passenger.setEmail("supermail" + i + "@yahoo.com");
                    passenger.setBalance(new BigDecimal(100));
                    passenger.setCity(city);

                    Accesscard ps = new Accesscard();
                    ps.setValidityDate(LocalDateTime.now());
                    ps.setActivationDate(LocalDateTime.now());
                    ps.setAcId(i);

                    passenger.setCard(ps);

                    return passenger;

                }));
    }

    /**
     * generateJourneys
     *
     * @param operators -
     * @param passengers -
     * @return List<Journey> -
     */
    private List<Journey> generateJourneys(List<Operator> operators, List<Passenger> passengers) {

        return transportService.addJourney(
                CollectionUtils.objGenerateSet(50, i -> {

                    Operator randOperator = CollectionUtils.getRandomElement(operators, i);
                    Passenger randPassenger = CollectionUtils.getRandomElement(passengers, i);

                    Journey journey = new Journey();
                    journey.setIdentifer(Utils.getRandString(60));
                    journey.setOperator(randOperator);
                    journey.setPassenger(randPassenger);
                    journey.setDateStart(LocalDateTime.now());
                    journey.setLastAction(LocalDateTime.now());

                    return journey;

                }));

    }

    /**
     * generateJourneyleg
     *
     * @param operator    -
     * @param passengers    -
     * @param journeys -
     * @return List<Journeyleg> -
     */
    private List<Journeyleg> generateJourneyleg(List<Operator> operator, List<Passenger> passengers, List<Journey> journeys) {

        return transportService.addJourneylegs(CollectionUtils.objGenerateSet(50, i -> {

            Operator randOperator = CollectionUtils.getRandomElement(operator, i);
            Passenger randPassenger = CollectionUtils.getRandomElement(passengers, i);
            Journey randJourney = CollectionUtils.getRandomElement(journeys, i);

            return new Journeyleg(
                    null,
                    Utils.getRandString(60),
                    false,
                    LocalDateTime.now(),
                    LocalDateTime.now(),
                    randOperator,
                    randPassenger,
                    randJourney,
                    null
            );
        }));

    }

    /**
     * generateActions
     *
     * @param journeylegs -
     * @return List<Action> -
     */
    private List<Action> generateActions(List<Journeyleg> journeylegs, Set<Point> points) {

        List<Action> actions = CollectionUtils.objGenerate(50, i -> {

            Journeyleg randJourneyleg = CollectionUtils.getRandomElement(journeylegs, i);
            Point randPoint = CollectionUtils.getRandomElement(new ArrayList<>(points), i);

            Action action = new Action();
            action.setJourneyleg(randJourneyleg);
            action.setIdentifier(Utils.getRandString(60));
            action.setType(Action.TYPE.TOUCH_IN);
            action.setDate(LocalDateTime.now());
            action.setPoint(randPoint);

            return action;

        });

        return transportService.addActions(actions);

    }

}