package ca.bc.gov.educ.api.coreg.repository.v1;

import org.springframework.data.jpa.repository.JpaRepository;
import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;

public interface CourseInformationRepository extends JpaRepository<CoursesEntity, String> {

}
