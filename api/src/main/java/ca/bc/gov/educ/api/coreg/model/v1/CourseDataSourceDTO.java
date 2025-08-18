package ca.bc.gov.educ.api.coreg.model.v1;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseDataSourceDTO {
    private Long id;
    private String affectedColumn;
    private String oldValue;
    private String newValue;
}
