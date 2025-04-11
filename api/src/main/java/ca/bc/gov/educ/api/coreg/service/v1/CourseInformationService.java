package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.coreg.model.v1.CourseCodeEntity;
import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseCodeMappingRepository;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseInformationRepository;
import ca.bc.gov.educ.api.coreg.util.JsonUtil;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
@AllArgsConstructor
public class CourseInformationService {

    private final CourseInformationSearchService courseInformationSearchService;

    private final CourseInformationRepository courseInformationRepository;

    private final CourseCodeMappingRepository courseCodeMappingRepository;

    public CoursesEntity getCourseInformation(String courseID) {
        val optionalCoursesEntity = courseInformationRepository.findById(new BigInteger(courseID));
        optionalCoursesEntity.orElseThrow(() -> new EntityNotFoundException(CoursesEntity.class, "courseID", courseID.toString()));
        return optionalCoursesEntity.get();
    }

    public CoursesEntity getCourseInformationByExternalCode(String externalCode){
        List<CourseCodeEntity> curSchoolEntityOptional = courseCodeMappingRepository.findByExternalCode(externalCode);

        if(!curSchoolEntityOptional.isEmpty()){
            return curSchoolEntityOptional.get(0).getCoursesEntity();
        }
        return null;
    }

    public CompletableFuture<Page<CoursesEntity>> getCourseInformationByCriteria(Integer pageNumber, Integer pageSize, String sortCriteriaJson, String searchCriteriaListJson) {
        final List<Sort.Order> sorts = new ArrayList<>();
        Specification<CoursesEntity> specs = courseInformationSearchService
                .setSpecificationAndSortCriteria(
                        sortCriteriaJson,
                        searchCriteriaListJson,
                        JsonUtil.mapper,
                        sorts
                );
        return courseInformationSearchService
                .findAll(specs, pageNumber, pageSize, sorts);
    }
}
