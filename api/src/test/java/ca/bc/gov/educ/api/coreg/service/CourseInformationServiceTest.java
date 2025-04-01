package ca.bc.gov.educ.api.coreg.service;

import ca.bc.gov.educ.api.coreg.controller.v1.BaseIntegrationTest;
import ca.bc.gov.educ.api.coreg.filter.CourseInformationFilterSpecs;
import ca.bc.gov.educ.api.coreg.model.v1.CourseCodeEntity;
import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseCodeMappingRepository;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseInformationRepository;
import ca.bc.gov.educ.api.coreg.service.v1.CourseInformationSearchService;
import ca.bc.gov.educ.api.coreg.service.v1.CourseInformationService;
import ca.bc.gov.educ.api.coreg.util.JsonUtil;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class CourseInformationServiceTest extends BaseIntegrationTest {

    @Mock
    CourseInformationSearchService courseInformationSearchService;

    @Mock
    CourseInformationRepository courseInformationRepository;

    @Mock
    CourseCodeMappingRepository courseCodeMappingRepository;

    @Mock
    CourseInformationFilterSpecs courseFilterSpecs;

    @InjectMocks
    CourseInformationService courseInformationService;

    @Test
    public void testGetCourseInformation() {
        BigInteger courseId = new BigInteger("8989898");
        CoursesEntity coursesEntity = createCourseEntity(courseId);

        when(courseInformationRepository.findById(courseId)).thenReturn(Optional.ofNullable(coursesEntity));
        CoursesEntity result = courseInformationService.getCourseInformation(courseId);
        assertThat(result).isNotNull();
        assertThat(result.getCourseID()).isEqualTo(courseId);
    }

    @Test
    public void testGetCourseInformationByExternalCode() {
        BigInteger courseId = new BigInteger("8989898");
        String externalCode = "ABC123";
        CourseCodeEntity courseCodeEntity = createCourseCodeEntity(externalCode);
        CoursesEntity coursesEntity = createCourseEntity(courseId);
        coursesEntity.setCourseCode(Set.of(courseCodeEntity));
        courseCodeEntity.setCoursesEntity(coursesEntity);

        when(courseCodeMappingRepository.findByExternalCode(externalCode)).thenReturn(Optional.ofNullable(courseCodeEntity));
        CoursesEntity result = courseInformationService.getCourseInformationByExternalCode(externalCode);
        assertThat(result).isNotNull();
        assertThat(result.getCourseID()).isEqualTo(courseId);
    }

    @Test
    public void testGetCourseInformationByCriteria() {
        BigInteger courseId = new BigInteger("8989898");
        String externalCode = "ABC123";
        CourseCodeEntity courseCodeEntity = createCourseCodeEntity(externalCode);
        CoursesEntity coursesEntity = createCourseEntity(courseId);
        coursesEntity.setCourseCode(Set.of(courseCodeEntity));
        courseCodeEntity.setCoursesEntity(coursesEntity);

        String sortCriteriaJson = null;
        String searchCriteriaListJson = "[{\"condition\":null,\"searchCriteriaList\":[{\"key\":\"courseId\",\"operation\":\"eq\",\"value\":\""+courseId+"\",\"valueType\":\"STRING\",\"condition\":\"AND\"}]}]";
        Specification<CoursesEntity> courseEntitySpecs = courseInformationSearchService.setSpecificationAndSortCriteria(sortCriteriaJson, searchCriteriaListJson, JsonUtil.mapper, new ArrayList<>());
        final List<Sort.Order> sorts = new ArrayList<>();

        Page<CoursesEntity> mockPage = new PageImpl<>(Arrays.asList(coursesEntity));

        when(courseInformationSearchService.findAll(courseEntitySpecs, 0, 10, sorts)).thenReturn(CompletableFuture.completedFuture(mockPage));
        CompletableFuture<Page<CoursesEntity>> result = courseInformationService.getCourseInformationByCriteria(0,10, sortCriteriaJson, searchCriteriaListJson);

        assertThat(result).isNotNull();
        assertThat(result.join().getContent().get(0).getCourseID()).isEqualTo(courseId);
    }
}
