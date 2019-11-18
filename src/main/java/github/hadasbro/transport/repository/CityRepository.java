package github.hadasbro.transport.repository;

import github.hadasbro.transport.domain.location.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings({"unused"})
@Repository
public interface CityRepository extends JpaRepository<City, Long> {}