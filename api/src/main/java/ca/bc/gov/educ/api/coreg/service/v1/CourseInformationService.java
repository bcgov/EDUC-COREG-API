package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.coreg.model.v1.CourseCodeEntity;
import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseCodeMappingRepository;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseInformationRepository;
import ca.bc.gov.educ.api.coreg.struct.v1.CourseCode;
import lombok.val;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CourseInformationService {

    private final CourseInformationRepository courseInformationRepository;

    private final CourseCodeMappingRepository courseCodeMappingRepository;

    public CourseInformationService(CourseInformationRepository courseInformationRepository, CourseCodeMappingRepository courseCodeMappingRepository) {
        this.courseInformationRepository = courseInformationRepository;
        this.courseCodeMappingRepository = courseCodeMappingRepository;
    }

    public CoursesEntity getCourseInformation(String courseID) {
        val optionalCoursesEntity = courseInformationRepository.findById(courseID);
        optionalCoursesEntity.orElseThrow(() -> new EntityNotFoundException(CoursesEntity.class, "courseID", courseID));
        return optionalCoursesEntity.get();
    }

    public CoursesEntity getCourseInformationByExternalCode(String externalCode){
        Optional<CourseCodeEntity> curSchoolEntityOptional = courseCodeMappingRepository.findByExternalCode(externalCode);

         //optional<CoursesEntity> = courseInformationRepository.findByExternalCode(externalCode);
         return curSchoolEntityOptional.get().getCoursesEntity();
    }
}
