package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseCodeMappingRepository extends JpaRepository<CourseCodeEntity, String> {

    Optional<CourseCodeEntity> findByExternalCode(String externalCode);

}
