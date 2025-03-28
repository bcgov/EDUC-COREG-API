package ca.bc.gov.educ.api.coreg.controller.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramEntity;
import ca.bc.gov.educ.api.coreg.service.v1.GraduationProgramService;
import ca.bc.gov.educ.api.coreg.struct.v1.Courses;
import ca.bc.gov.educ.api.coreg.struct.v1.GraduationProgram;
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
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class GraduationProgramAPIControllerTest extends BaseIntegrationTest{

    @Mock
    private GraduationProgramService graduationProgramService;

    @InjectMocks
    private GraduationProgramAPIController graduationProgramAPIController;

    @Test
    public void testGetGraduationProgramBySearchCriteria_ProgramRequirementName_Equal() throws ExecutionException, InterruptedException {
        String gradProgramID = "PRG123";
        Integer pageNumber = 0;
        Integer pageSize = 10;
        GraduationProgramEntity graduationProgramEntity = createGraduationProgramEntity(gradProgramID);
        String sortCriteriaJson = null;
        String searchCriteriaListJson = "[[{\"condition\":null,\"searchCriteriaList\":[{\"key\":\"gradProgramID\",\"operation\":\"eq\",\"value\":\""+gradProgramID+"\",\"valueType\":\"STRING\",\"condition\":\"AND\"}]}]";
        List<Sort.Order> sorts = new ArrayList<>();
        Page<GraduationProgramEntity> mockPage = new PageImpl<>(Arrays.asList(graduationProgramEntity));
        CompletableFuture<Page<GraduationProgramEntity>> mockFuture = CompletableFuture.completedFuture(mockPage);
        Specification<GraduationProgramEntity> graduationProgramEntitySpecs = graduationProgramService.setSpecificationAndSortCriteria(sortCriteriaJson, searchCriteriaListJson, JsonUtil.mapper, new ArrayList<>());

        when(graduationProgramService.findAll(graduationProgramEntitySpecs, pageNumber, pageSize, sorts))
                .thenReturn(mockFuture);
        CompletableFuture<Page<GraduationProgram>> resultFuture = graduationProgramAPIController.findAll(
                pageNumber,
                pageSize,
                sortCriteriaJson,
                searchCriteriaListJson);
        Page<GraduationProgram> result = resultFuture.get();
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
    }

}
