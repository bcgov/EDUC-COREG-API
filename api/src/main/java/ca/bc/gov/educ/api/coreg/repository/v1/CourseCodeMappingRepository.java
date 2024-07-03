package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseCodeEntity;
import ca.bc.gov.educ.api.coreg.struct.v1.CourseCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CourseCodeMappingRepository extends JpaRepository<CourseCodeEntity, String> {

    Optional<CourseCodeEntity> findByExternalCode(String externalCode);

}
