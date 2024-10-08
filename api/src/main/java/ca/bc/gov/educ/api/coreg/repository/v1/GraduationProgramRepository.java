package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface GraduationProgramRepository extends JpaRepository<GraduationProgramEntity, UUID>, JpaSpecificationExecutor<GraduationProgramEntity> {
}
