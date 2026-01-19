package ca.bc.gov.educ.api.coreg.model.v1;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import java.math.BigInteger;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@DynamicUpdate
@Table(name = "CRSE_COURSE_STATUSES" , schema = "COREG")
public class CourseStatusEntity {
    @Id
    @Column(name = "CRSSTTS_ID", unique = true, updatable = false, columnDefinition = "BIGINTEGER")
    private BigInteger crsStatusID;

    @ManyToOne(optional = true, targetEntity = CoursesEntity.class)
    @JoinColumn(name = "CRS_ID", referencedColumnName = "CRS_ID")
    CoursesEntity coursesEntity;
    
}
