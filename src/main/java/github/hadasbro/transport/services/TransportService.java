package github.hadasbro.transport.services;

import github.hadasbro.transport.components.RedisComponent;
import github.hadasbro.transport.domain.journey.Action;
import github.hadasbro.transport.domain.journey.Journey;
import github.hadasbro.transport.domain.journey.Journeyleg;
import github.hadasbro.transport.domain.location.Point;
import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.domain.transport.Operator;
import github.hadasbro.transport.exceptions.ApiException;
import github.hadasbro.transport.repository.*;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log
@Service
@SuppressWarnings({"unused","ConstantConditions"})
public class TransportService {

    @PersistenceContext(name = "default")
    @Qualifier( "entityManager")
    private EntityManager em;

    @Autowired
    private PassengerRepository passengerRepository;

    @Autowired
    private JourneyRepository journeyRepository;

    @Autowired
    private JourneylegRepository journeylegRepository;

    @Autowired
    private ActionRepository actionRepository;

    @Autowired
    private RedisComponent redisComponent;

    @Autowired
    private PointRepository pointRepository;

    /**
     * getJourneylegOrActions
     *
     * @param journeyIdentifer -
     * @param actionIdentifier -
     * @return  List<Journeyleg>
     */
    public List<Journeyleg> getJourneylegOrActions(String journeyIdentifer, String actionIdentifier) {

        return em.createQuery(
                 "SELECT jleg FROM Journeyleg jleg " +
                    "JOIN jleg.actions act " +
                    "WHERE jleg.identifer = :identifer OR act.identifier = :identifier",
                    Journeyleg.class
                )
                .setParameter("identifer", journeyIdentifer)
                .setParameter("identifier", actionIdentifier)
                .getResultList();

    }

    /**
     * getActionsAndJourneyleg
     * @param actionIdentifier -
     * @return Journeyleg
     */
    public List<Action> getActionsAndJourneyleg(String actionIdentifier) {

        return em.createQuery(
                "SELECT act FROM Journeyleg jleg " +
                        "JOIN jleg.actions act " +
                        "WHERE act.identifier = :actionIdentifier",
                    Action.class
                )
                .setParameter("actionIdentifier", actionIdentifier)
                .getResultList();

    }

    /**
     * getJourneyByIdentifer
     *
     * @param identifer -
     * @return Optional<Journey> -
     */
    public Optional<Journey> getJourneyByIdentifer(String identifer) {
         return journeyRepository.findFirstByIdentifer(identifer);
    }

    /**
     * addActions
     *
     * @param actions -
     * @return List<Action> -
     */
    public List<Action> addActions(List<Action> actions){
        return actionRepository.saveAll(actions);
    }

    /**
     * addJourneyleg
     *
     * @param journeyleg -
     */
    public void addJourneyleg(Journeyleg journeyleg) {
        journeylegRepository.save(journeyleg);
    }

    /**
     * addJourneylegs
     *
     * @param journeylegs -
     * @return List<Journeyleg> -
     */
    public List<Journeyleg> addJourneylegs(Set<Journeyleg> journeylegs) {
        return journeylegRepository.saveAll(journeylegs);
    }

    /**
     * addJourneys
     *
     * @param journeys -
     * @return List<Journey> -
     */
    public List<Journey> addJourneys(Set<Journey> journeys){
        return journeyRepository.saveAll(journeys);
    }

    /**
     * addJourney
     *
     * @param journey -
     * @return Journey -
     */
    public Journey addJourney(Journey journey){
        return journeyRepository.save(journey);
    }

    /**
     * addPoints
     *
     * @param points -
     */
    public Set<Point> addPoints(Set<Point> points){
        return new HashSet<>(pointRepository.saveAll(points));
    }

    /**
     * getJourney
     *
     * @param id -
     * @return Optional<Journey> -
     */
    public Optional<Journey> getJourney(Long id){
        return journeyRepository.findById(id);
    }

    /**
     * logPassengerChange
     *
     * @param passenger -
     */
    public void logPassengerChange(Passenger passenger){
        log.info("# balance_change | real: " + passenger.getBalance());
    }

    /**
     * checkJourneyRestrictions
     *
     * @param journey -
     * @throws ApiException -
     */
    public void checkJourneyRestrictions(Journey journey) throws ApiException {

        if (journey.getFinished()) {
            throw new ApiException(ApiException.CODES.JOURNEY_NOACTIVE);
        }

    }

    /**
     * getPointById
     * @param id -
     * @return Optional<Point> -
     */
    public Optional<Point> getPointById(Long id) {
        return pointRepository.findById(id);
    }

    /**
     * checkJourneyLimits
     *
     * @param journey -
     * @param passenger -
     * @throws ApiException -
     */
    public void checkJourneyLimits(Journey journey, Passenger passenger) throws ApiException {
        if(1==12)
            throw new ApiException(ApiException.CODES.JOURNEY_EXPIRED_FINISHED);
    }

    /**
     * checkJourneyForOperator
     *
     * @param journey -
     * @param operator -
     * @throws ApiException -
     */
    public void checkJourneyForOperator(Journey journey, Operator operator)  throws ApiException {
        if(!journey.getOperator().equals(operator))
            throw new ApiException(ApiException.CODES.JOURNEY_OPERATOR);
    }

    /**
     * checkJourneyForPassenger
     *
     * @param journey -
     * @param passenger -
     * @throws ApiException -
     */
    public void checkJourneyForPassenger(Journey journey, Passenger passenger)  throws ApiException {
        if(!journey.getPassenger().equals(passenger))
            throw new ApiException(ApiException.CODES.JOURNEY_PASSENGER);
    }

    /*
    ################################################################################
    ############################ api action methods ################################
    ################################################################################
     */

    /**
     * pingJourney
     *
     * @param journey -
     */
    public void pingJourney(Journey journey) {
        journey.setLastAction(null);
        journeyRepository.saveAndFlush(journey);
    }

    /**
     * addAction
     *
     * @param action -
     */
    public void addAction(Action action){
        actionRepository.save(action);
    }

    /**
     * refreshJourneyDataWebsocket
     *
     * @param passenger -
     */
    public void refreshJourneyDataWebsocket(Passenger passenger) {}

    /**
     * createJourney
     *
     * @param journey -
     */
    public void createJourney(Journey journey) {
        journeyRepository.save(journey);
    }

}