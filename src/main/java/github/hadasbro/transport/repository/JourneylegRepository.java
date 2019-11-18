package github.hadasbro.transport.repository;

import github.hadasbro.transport.domain.journey.Journeyleg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JourneylegRepository extends JpaRepository<Journeyleg, Long> {}
