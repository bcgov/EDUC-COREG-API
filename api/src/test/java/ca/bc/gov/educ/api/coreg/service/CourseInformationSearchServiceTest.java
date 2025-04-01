package ca.bc.gov.educ.api.coreg.service;

import ca.bc.gov.educ.api.coreg.controller.v1.BaseIntegrationTest;
import ca.bc.gov.educ.api.coreg.exception.InstituteAPIRuntimeException;
import ca.bc.gov.educ.api.coreg.filter.CourseInformationFilterSpecs;
import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseInformationRepository;
import ca.bc.gov.educ.api.coreg.service.v1.CourseInformationSearchService;
import ca.bc.gov.educ.api.coreg.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class CourseInformationSearchServiceTest extends BaseIntegrationTest {

    @InjectMocks
    CourseInformationSearchService courseInformationSearchService;

    @Mock
    CourseInformationRepository courseInformationRepository;
    @Mock
    CourseInformationFilterSpecs courseFilterSpecs;

    @Test
    public void findAll_shouldReturn() {
        BigInteger courseId = new BigInteger("8989898");
        CoursesEntity coursesEntity = createCourseEntity(courseId);

        String sort = "{ \"courseID\": \"ASC\" }";
        String searchParams = "[{\"condition\":null,\"searchCriteriaList\":[{\"key\":\"courseID\",\"operation\":\"eq\",\"value\":\"CRSE123\",\"valueType\":\"STRING\",\"condition\":\"AND\"}]}]";
        Specification<CoursesEntity> courseEntitySpecs = courseInformationSearchService.setSpecificationAndSortCriteria(sort, searchParams, JsonUtil.mapper, new ArrayList<>());
        final List<Sort.Order> sorts = new ArrayList<>();

        Page<CoursesEntity> mockPage = new PageImpl<>(Arrays.asList(coursesEntity));

        when(courseInformationRepository.findAll(courseEntitySpecs, PageRequest.of(0, 10, Sort.by("courseID").ascending()))).thenReturn(mockPage);
        Page<CoursesEntity> coursesEntityPage = courseInformationRepository.findAll(courseEntitySpecs, PageRequest.of(0, 10, Sort.by("courseID").ascending()));
        Assert.assertNotNull(coursesEntityPage);
        Assert.assertEquals(1, coursesEntityPage.getTotalElements());
        CompletableFuture<Page<CoursesEntity>> resultFuture = courseInformationSearchService.findAll(courseEntitySpecs, 0, 10, sorts);
        assertNotNull(resultFuture);
        resultFuture.join();
        Assert.assertEquals(true, resultFuture.isDone());
    }

    @Test
    public void setSpecificationAndSortCriteria_givenValidData_shouldReturnOk() {
        String sort = "{ \"courseID\": \"ASC\" }";
        String searchParams = "[{\"condition\":null,\"searchCriteriaList\":[{\"key\":\"courseID\",\"operation\":\"eq\",\"value\":\"8989898\",\"valueType\":\"STRING\",\"condition\":\"AND\"}]}]";
        Specification<CoursesEntity> courseEntitySpecs = courseInformationSearchService.setSpecificationAndSortCriteria(sort, searchParams, JsonUtil.mapper, new ArrayList<>());
        Assert.assertNotNull(courseEntitySpecs);
    }

    @Test
    public void setSpecificationAndSortCriteria_givenInvalidData_shouldThrowInstituteAPIRuntimeException() {
        final List<Sort.Order> sorts = new ArrayList<>();
        Assert.assertThrows(InstituteAPIRuntimeException.class, () -> courseInformationSearchService.setSpecificationAndSortCriteria(null, "{ \"novalue\": \"ASC\" }", JsonUtil.mapper, sorts));
    }

}
