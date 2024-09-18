package ca.bc.gov.educ.api.coreg.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "CRSE_GRADUATION_PROGRAMS" , schema = "COREG")
@JsonIgnoreProperties(ignoreUnknown = true)
public class GraduationProgramEntity {
    @Id
    @Column(name = "GRDPRG_ID", unique = true, updatable = false)
    private String gradProgramID;
    @Basic
    @Column(name = "NAME")
    private String gradProgramName;
    @Basic
    @Column(name = "SHORT_NAME")
    private String gradProgramShortName;
    @PastOrPresent
    @Column(name = "START_DATE")
    private LocalDateTime startDate;
    @Basic
    @Column(name = "END_DATE")
    private LocalDateTime endDate;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @OneToMany(mappedBy = "graduationProgramEntity", fetch = FetchType.EAGER, cascade = CascadeType.DETACH, targetEntity = GraduationProgramRequirementEntity.class)
    private Set<GraduationProgramRequirementEntity> gradProgramRequirement;
}
