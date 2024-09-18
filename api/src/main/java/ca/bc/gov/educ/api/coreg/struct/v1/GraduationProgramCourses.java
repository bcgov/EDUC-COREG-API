package ca.bc.gov.educ.api.coreg.struct.v1;

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
public class GraduationProgramCourses implements Serializable {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 2L;

    private String gradProgramCourseID;

    private String gradProgramCourseType;

    private String courseID;

    private String gradProgramID;

    private String gradProgramRequirementID;
}
