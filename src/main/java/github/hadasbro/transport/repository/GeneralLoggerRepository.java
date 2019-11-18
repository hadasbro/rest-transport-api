package github.hadasbro.transport.repository;

import github.hadasbro.transport.domain.logger.GeneralLogger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GeneralLoggerRepository extends JpaRepository<GeneralLogger, Long> {}

