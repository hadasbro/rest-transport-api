package github.hadasbro.transport.repository;

import github.hadasbro.transport.domain.location.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@SuppressWarnings({"unused"})
@Repository
public interface CountryRepository extends JpaRepository<Country, Long> {}