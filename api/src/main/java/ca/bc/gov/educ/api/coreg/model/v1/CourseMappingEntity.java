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
@Table(name = "CRSE_COURSE_CODE_MAPPINGS" , schema = "COREG")
public class CourseMappingEntity {

    @Id
    @Column(name = "CRSCDMAP_ID", unique = true, updatable = false, columnDefinition = "BIGINTEGER")
    private BigInteger crscdmapID;

    @Basic
    @Column(name = "CRS_ID")
    private String courseID;

    @Basic
    @Column(name = "EXTERNAL_CODE")
    private String externalCode;

    @Basic
    @Column(name = "ORIGINATING_SYSTEM_CHAR_ID")
    private Integer originatingSystem;
}
