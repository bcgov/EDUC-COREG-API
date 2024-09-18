package ca.bc.gov.educ.api.coreg.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigInteger;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "CRSE_GRADUATION_PROGRAM_COURSS" , schema = "COREG")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraduationProgramCoursesEntity {

    @Id
    @Column(name = "GRDPRGCRS_ID", unique = true, updatable = false, columnDefinition = "BIGINTEGER")
    private BigInteger gradProgramCourseID;

    @Basic
    @Column(name = "GRDPRGCRS_TYPE")
    private String gradProgramCourseType;

    @ManyToOne(optional = true, targetEntity = GraduationProgramRequirementEntity.class)
    @JoinColumn(name = "GRDPRGREQ_ID", referencedColumnName = "GRDPRGREQ_ID")
    GraduationProgramRequirementEntity graduationProgramRequirementEntity;

    @Basic
    @Column(name = "CRS_ID")
    private String courseID;

    @Basic
    @Column(name = "GRDPRG_ID")
    private String gradProgramID;

}
