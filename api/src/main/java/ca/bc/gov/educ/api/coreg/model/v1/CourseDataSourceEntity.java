package ca.bc.gov.educ.api.coreg.model.v1;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "CRSE_COURSE_DATA_SOURCES", schema = "COREG")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CourseDataSourceEntity {

    @Id
    @Column(name = "CRSDTSRC_ID")
    private Long id;

    @Column(name = "AFFECTED_COLUMN", nullable = false)
    private String affectedColumn;

    @Column(name = "NEW_VALUE", nullable = false)
    private String newValue;

    @Column(name = "OLD_VALUE", nullable = false)
    private String oldValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CRSREGEV_ID", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private CourseRegistryEventEntity courseRegistryEventEntity;
}
