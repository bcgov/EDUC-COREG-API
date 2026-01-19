package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseManagementRolesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;

@Repository
public interface CourseManagementRolesRepository extends JpaRepository<CourseManagementRolesEntity, BigInteger>, JpaSpecificationExecutor<CourseManagementRolesEntity> {
    

}
