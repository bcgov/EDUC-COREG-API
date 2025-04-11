package ca.bc.gov.educ.api.coreg.controller.v1;

import ca.bc.gov.educ.api.coreg.model.v1.*;

import java.math.BigInteger;
import java.util.Set;
import java.util.UUID;

public abstract class BaseIntegrationTest {

    public CourseCodeEntity createCourseCodeEntity(String externalCode) {
        CourseCodeEntity courseCodeEntity = new CourseCodeEntity();
        courseCodeEntity.setExternalCode(externalCode);
        courseCodeEntity.setOriginatingSystem(39);
        courseCodeEntity.setCrscdmapID(new BigInteger("111"));
        return courseCodeEntity;
    }

    public CoursesEntity createCourseEntity(String courseId) {
        CoursesEntity coursesEntity = new CoursesEntity();
        coursesEntity.setCourseID(new BigInteger(courseId));
        return coursesEntity;
    }

    public GraduationProgramEntity createGraduationProgramEntity(String gradProgramID) {
        GraduationProgramCoursesEntity graduationProgramCoursesEntity = new GraduationProgramCoursesEntity();
        graduationProgramCoursesEntity.setGradProgramCourseType("TEST");
        GraduationProgramRequirementEntity graduationProgramRequirementEntity = new GraduationProgramRequirementEntity();
        graduationProgramRequirementEntity.setGradProgramRequirementID(new BigInteger("1"));
        graduationProgramRequirementEntity.setGraduationProgramCourses(Set.of(graduationProgramCoursesEntity));
        GraduationProgramEntity graduationProgramEntity = new GraduationProgramEntity();
        graduationProgramEntity.setGradProgramID(new BigInteger(gradProgramID));
        graduationProgramEntity.setGradProgramRequirement(Set.of(graduationProgramRequirementEntity));
        return graduationProgramEntity;
    }
}
