package github.hadasbro.transport.services;

import github.hadasbro.transport.domain.passenger.Passenger;
import github.hadasbro.transport.exceptions.ApiException;
import github.hadasbro.transport.repository.PassengerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@SuppressWarnings("unused")
public class PassengerService {

    @Autowired
    private PassengerRepository repository;

    @PersistenceContext(name = "default")
    @Qualifier( "entityManager")
    private EntityManager em;

    /**
     * countByActive
     *
     * @param active -
     * @return Long -
     */
    public Long countByActive(boolean active) {
        return repository.countByActive(active);
    }

    /**
     * findById
     *
     * @param id -
     * @return Optional<Passenger> -
     */
    public Optional<Passenger> findById(Long id) {
        return repository.findById(id);
    }

    /**
     * findOneById
     *
     * @param id -
     * @return Passenger -
     */
    public Passenger findOneById(Long id) {
        Optional<Passenger> opt = findById(id);
        return opt.orElse(null);
    }

    /**
     * add
     * @param passengers -
     * @return List<Passenger>
     */
    public List<Passenger> add(Set<Passenger> passengers) {
        return repository.saveAll(passengers);
    }

    /**
     * checkPassengerRestrictions
     *
     * @return boolean -
     * @throws ApiException -
     */
    public void checkPassengerRestrictions(Passenger passenger) throws ApiException {

        if(!passenger.isActive()){
            throw new ApiException(ApiException.CODES.PASSENGER_NOACTIVE);
        }

        if(passenger.isBlocked()){
            throw new ApiException(ApiException.CODES.PASSENGER_BLOCKED);
        }

    }

    /**
     * checkPassengerLimits
     *
     * @return boolean -
     * @throws ApiException -
     */
    public void checkPassengerLimits(Passenger passenger) throws ApiException {

        if (passenger.getCard().getValidityDate().isAfter(LocalDateTime.now())) {
            throw new ApiException(ApiException.CODES.LIMIT_DAY);
        }

    }

    /**
     * passengerHasMoney
     *
     * @param passenger -
     * @param amount -
     * @return boolean
     */
    public boolean passengerHasMoney(Passenger passenger, Double amount) {
            return true;
    }

}