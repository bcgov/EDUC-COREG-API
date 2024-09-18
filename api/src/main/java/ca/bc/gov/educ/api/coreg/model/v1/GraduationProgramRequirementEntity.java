package ca.bc.gov.educ.api.coreg.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigInteger;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "CRSE_GRADUATION_PROGRAM_RQRMNT" , schema = "COREG")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraduationProgramRequirementEntity {

    @Id
    @Column(name = "GRDPRGREQ_ID", unique = true, updatable = false, columnDefinition = "BIGINTEGER")
    private BigInteger gradProgramRequirementID;

    @Basic
    @Column(name = "NAME")
    private String gradProgramRequirementName;

    @ManyToOne(optional = true, targetEntity = GraduationProgramEntity.class)
    @JoinColumn(name = "GRDPRG_ID", referencedColumnName = "GRDPRG_ID")
    GraduationProgramEntity graduationProgramEntity;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "graduationProgramRequirementEntity", fetch = FetchType.EAGER, cascade = CascadeType.DETACH, targetEntity = GraduationProgramCoursesEntity.class)
    private Set<GraduationProgramCoursesEntity> graduationProgramCourses;

}
