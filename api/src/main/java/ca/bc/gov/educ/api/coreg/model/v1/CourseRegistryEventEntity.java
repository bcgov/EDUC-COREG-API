package ca.bc.gov.educ.api.coreg.model.v1;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "CRSE_COURSE_REGISTRY_EVENTS", schema = "COREG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseRegistryEventEntity {

    @Id
    @Column(name = "CRSREGEV_ID")
    private Long id;

    @Column(name = "AFFECTED_TABLE", nullable = false)
    private String affectedTable;

    @Column(name = "AFFECTED_ID", nullable = false)
    private Long affectedId;

    @Column(name = "REGISTRY_EVENT_TYPE_CHAR_ID", nullable = false)
    private Long registryEventTypeCharId;

    @Column(name = "CREATED_DATE", nullable = false)
    private LocalDateTime createdDate;

    @Column(name = "CREATED_USER", nullable = false)
    private String createdUser;

    @OneToMany(mappedBy = "courseRegistryEventEntity", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<CourseDataSourceEntity> dataSourceEntities;
}
