package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseCharacteristicsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.math.BigInteger;

public interface CourseCharacteristicsRepository extends JpaRepository<CourseCharacteristicsEntity, BigInteger>, JpaSpecificationExecutor<CourseCharacteristicsEntity> {
}
