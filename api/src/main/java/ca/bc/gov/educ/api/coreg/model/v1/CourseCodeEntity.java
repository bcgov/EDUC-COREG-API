package ca.bc.gov.educ.api.coreg.model.v1;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicUpdate
@Table(name = "CRSE_COURSE_CODE_MAPPINGS" , schema = "COREG")
public class CourseCodeEntity {


    @Id
    @Column(name = "CRSCDMAP_ID", unique = true, updatable = false, columnDefinition = "BIGINTEGER")
    private String crscdmapID;
    @ManyToOne(optional = true, targetEntity = CoursesEntity.class)
    @JoinColumn(name = "CRS_ID", referencedColumnName = "CRS_ID")
    CoursesEntity coursesEntity;

    @Basic
    @Column(name = "EXTERNAL_CODE")
    private String externalCode;

    @Basic
    @Column(name = "ORIGINATING_SYSTEM_CHAR_ID")
    private String originatingSystem;
}
