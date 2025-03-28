package ca.bc.gov.educ.api.coreg.controller.v1;


import ca.bc.gov.educ.api.coreg.model.v1.CourseCodeEntity;
import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import ca.bc.gov.educ.api.coreg.service.v1.CourseInformationSearchService;
import ca.bc.gov.educ.api.coreg.service.v1.CourseInformationService;
import ca.bc.gov.educ.api.coreg.struct.v1.Courses;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class CourseInformationAPIControllerTest extends BaseIntegrationTest{

    @Mock
    private CourseInformationService courseInformationService;

    @Mock
    private CourseInformationSearchService courseInformationSearchService;

    @InjectMocks
    private CourseInformationAPIController courseInformationAPIController;

    @Test
    public void testGetCourseInformation_Found() {
        String courseId = "CRSE123";
        CoursesEntity coursesEntity = createCourseEntity(courseId);

        when(courseInformationService.getCourseInformation(courseId)).thenReturn(coursesEntity);
        Courses result = courseInformationAPIController.getCourseInformation(courseId);
        assertThat(result).isNotNull();
        assertThat(result.getCourseID()).isEqualTo(courseId);
    }

    @Test
    public void testGetCourseInformation_NotFound() {
        String courseId = "CRSE123";

        Courses result = courseInformationAPIController.getCourseInformation(courseId);
        assertThat(result).isNull();
    }

    @Test
    public void testGetCourseInformationByExternalCode_Found() {
        String courseId = "CRSE123";
        String externalCode = "ABC123";
        CourseCodeEntity courseCodeEntity = createCourseCodeEntity(externalCode);
        CoursesEntity coursesEntity = createCourseEntity(courseId);
        coursesEntity.setCourseCode(Set.of(courseCodeEntity));

        when(courseInformationService.getCourseInformationByExternalCode(externalCode)).thenReturn(coursesEntity);
        Courses result = courseInformationAPIController.getCourseInformationByExternalCode(externalCode);
        assertThat(result).isNotNull();
        assertThat(result.getCourseID()).isEqualTo(courseId);
    }

    @Test
    public void testGetCourseInformationByExternalCode_NotFound() {
        String externalCode = "ABC123";

        Courses result = courseInformationAPIController.getCourseInformationByExternalCode(externalCode);
        assertThat(result).isNull();
    }

    @Test
    public void testGetCourseInformationBySearchCriteria_CourseId_Equal() throws ExecutionException, InterruptedException {
        String courseId = "CRSE123";
        Integer pageNumber = 0;
        Integer pageSize = 10;
        CoursesEntity coursesEntity = createCourseEntity(courseId);
        String sortCriteriaJson = null;
        String searchCriteriaListJson = "[[{\"condition\":null,\"searchCriteriaList\":[{\"key\":\"courseId\",\"operation\":\"eq\",\"value\":\""+courseId+"\",\"valueType\":\"STRING\",\"condition\":\"AND\"}]}]";
        Page<CoursesEntity> mockPage = new PageImpl<>(Arrays.asList(coursesEntity));
        CompletableFuture<Page<CoursesEntity>> mockFuture = CompletableFuture.completedFuture(mockPage);
        when(courseInformationService.getCourseInformationByCriteria(pageNumber, pageSize, sortCriteriaJson,searchCriteriaListJson))
                .thenReturn(mockFuture);
        CompletableFuture<Page<Courses>> resultFuture = courseInformationAPIController.findAll(
                pageNumber,
                pageSize,
                sortCriteriaJson,
                searchCriteriaListJson);
        Page<Courses> result = resultFuture.get();
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }


}
