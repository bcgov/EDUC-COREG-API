package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface CourseRegistryEventRepository extends JpaRepository<CourseRegistryEventEntity, Long> {

    @EntityGraph(attributePaths = "dataSourceEntities")
    List<CourseRegistryEventEntity> findByAffectedTableAndCreatedDateAfter(
            String affectedTable, LocalDateTime fromDate);

    @EntityGraph(attributePaths = "dataSourceEntities")
    List<CourseRegistryEventEntity> findByCreatedDateAfter(LocalDateTime fromDate);
}

