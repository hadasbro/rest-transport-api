package github.hadasbro.transport.repository;

import github.hadasbro.transport.domain.logger.ApiLogger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoggerRepository extends JpaRepository<ApiLogger, Long> {}
