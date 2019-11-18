package github.hadasbro.transport.repository;

import github.hadasbro.transport.domain.location.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {}
