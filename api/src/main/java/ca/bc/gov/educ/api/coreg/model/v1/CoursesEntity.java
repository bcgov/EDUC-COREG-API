package ca.bc.gov.educ.api.coreg.model.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.PastOrPresent;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@DynamicUpdate
@Entity
@Table(name = "CRSE_COURSES" , schema = "COREG")
@JsonIgnoreProperties(ignoreUnknown = true)
public class CoursesEntity {
  @Id
  @Column(name = "CRS_ID", unique = true, updatable = false)
  private String courseID;
  @Basic
  @Column(name = "SIF_SUBJECT_CHAR_ID")
  private String sifSubjectCode;
  @Basic
  @Column(name = "COURSE_TITLE")
  private String courseTitle;
  @PastOrPresent
  @Column(name = "START_DATE")
  private LocalDateTime startDate;
  @Basic
  @Column(name = "END_DATE")
  private LocalDateTime endDate;

  @Basic
  @Column(name = "COMPLETION_END_DATE")
  private LocalDate completionEndDate;

  @Basic
  @Column(name = "GENERIC_CRSE_TYPE")
  private String genericCourseType;


  @Basic
  @Column(name = "PROGRAM_GUIDE_TITLE")
  private String programGuideTitle;

  @Basic
  @Column(name = "EXTERNAL_INDICATOR")
  private String externalIndicator;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "coursesEntity", fetch = FetchType.EAGER, cascade = CascadeType.DETACH, targetEntity = CourseCodeEntity.class)
  private Set<CourseCodeEntity> courseCode;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "LANGUAGE_TYPE_CHAR_ID", referencedColumnName = "CHAR_ID")
  private CourseCharacteristicsEntity courseCharacteristics;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToOne(cascade = CascadeType.ALL)
  @JoinColumn(name = "COURSE_CATEGORY_CHAR_ID", referencedColumnName = "CHAR_ID")
  private CourseCharacteristicsEntity courseCategory;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "coursesEntity", fetch = FetchType.EAGER, cascade = CascadeType.DETACH, targetEntity = CourseAllowableCreditEntity.class)
  private Set<CourseAllowableCreditEntity> courseAllowableCredit;

  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  @OneToMany(mappedBy = "coursesEntity", fetch = FetchType.EAGER, cascade = CascadeType.DETACH, targetEntity = RequiredCourseEntity.class)
  private Set<RequiredCourseEntity> requiredCourse;

}
