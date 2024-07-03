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
@Table(name = "CRSE_CHARACTERISTICS" , schema = "COREG")
public class CourseCharacteristicsEntity {

    @Id
    @Column(name = "CHAR_ID")
    String id;

    @Id
    @OneToOne(mappedBy = "courseCharacteristics")
    private CoursesEntity coursesEntity;

    @Basic
    @Column(name = "CHAR_TYPE")
    private String type;

    @Basic
    @Column(name = "CODE")
    private String code;

    @Basic
    @Column(name = "DESCRIPTION")
    private String description;

}
