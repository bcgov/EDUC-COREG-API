package ca.bc.gov.educ.api.coreg.model.v1;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CourseRegistryEventDTO {
    private String courseCode;
    private String courseLevel;
    private Long id;
    private String affectedTable;
    private Long affectedId;
    private Long registryEventTypeCharId;
    private LocalDateTime createdDate;
    private String createdUser;
    private List<CourseDataSourceDTO> dataSources;
}
