package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseMappingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseMappingRepository extends JpaRepository<CourseMappingEntity, String> {

    List<CourseMappingEntity> findByOriginatingSystem(String originatingSystemID);

}
