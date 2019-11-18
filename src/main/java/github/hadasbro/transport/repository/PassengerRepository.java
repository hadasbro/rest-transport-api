package github.hadasbro.transport.repository;

import github.hadasbro.transport.domain.passenger.Passenger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@SuppressWarnings("unused")
public interface PassengerRepository extends JpaRepository<Passenger, Long> {

    Passenger findByEmail(String email);

    List<Passenger> findByActive(boolean active);

    Long countByActive(boolean active);

}