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
public class GraduationProgramRequirement implements Serializable {
    /**
     * The constant serialVersionUID.
     */
    private static final long serialVersionUID = 3L;

    private String gradProgramRequirementID;

    private String gradProgramRequirementName;

    private String gradProgramID;

    @Valid
    private List<GraduationProgramCourses> graduationProgramCourses;

}
