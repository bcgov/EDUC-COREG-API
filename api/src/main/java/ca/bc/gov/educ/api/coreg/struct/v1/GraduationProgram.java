package ca.bc.gov.educ.api.coreg.struct.v1;

import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramCoursesEntity;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GraduationProgram implements Serializable {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;

    private String gradProgramID;

    private String gradProgramName;

    private String gradProgramShortName;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    @Valid
    private List<GraduationProgramRequirement> gradProgramRequirement;

}
