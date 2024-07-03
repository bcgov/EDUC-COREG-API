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
@Table(name = "CRSE_COURSE_ALLOWABLE_CREDITS" , schema = "COREG")
public class CourseAllowableCreditEntity {


    @Id
    @Column(name = "CAC_ID", unique = true, updatable = false, columnDefinition = "BIGINTEGER")
    private BigInteger cacID;

    @Basic
    @Column(name = "CREDIT_VALUE")
    private String creditValue;

    @ManyToOne(optional = true, targetEntity = CoursesEntity.class)
    @JoinColumn(name = "CRS_ID", referencedColumnName = "CRS_ID")
    CoursesEntity coursesEntity;

    @Basic
    @Column(name = "START_DATE")
    private String startDate;

    @Basic
    @Column(name = "END_DATE")
    private String endDate;

}
