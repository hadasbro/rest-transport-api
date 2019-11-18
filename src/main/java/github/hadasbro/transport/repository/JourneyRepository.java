package github.hadasbro.transport.repository;

import github.hadasbro.transport.domain.journey.Journey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JourneyRepository extends JpaRepository<Journey, Long> {
    Optional<Journey> findFirstByIdentifer(String token);
}