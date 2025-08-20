package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CoregCourseEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CoregCourseEventRepository extends JpaRepository<CoregCourseEvent, UUID> {
    Optional<CoregCourseEvent> findFirstByCrsregevIdOrderByCreateDateDesc(Long crsregevId);
    List<CoregCourseEvent> findAllByEventStatusOrderByCreateDate(String eventStatus);
}
