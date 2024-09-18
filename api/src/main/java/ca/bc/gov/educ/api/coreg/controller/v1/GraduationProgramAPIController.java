package ca.bc.gov.educ.api.coreg.controller.v1;

import ca.bc.gov.educ.api.coreg.endpoint.v1.GraduationProgramAPIEndpoint;
import ca.bc.gov.educ.api.coreg.mapper.v1.GraduationProgramMapper;
import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramEntity;
import ca.bc.gov.educ.api.coreg.service.v1.GraduationProgramService;
import ca.bc.gov.educ.api.coreg.struct.v1.GraduationProgram;
import ca.bc.gov.educ.api.coreg.util.JsonUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static ca.bc.gov.educ.api.coreg.util.JsonUtil.mapper;
@RestController
@Slf4j
public class GraduationProgramAPIController implements GraduationProgramAPIEndpoint {

    private static final GraduationProgramMapper mapper = GraduationProgramMapper.mapper;
    @Getter(AccessLevel.PRIVATE)
    private final GraduationProgramService gradProgramService;

    public GraduationProgramAPIController(GraduationProgramService gradProgramService) {
        this.gradProgramService = gradProgramService;
    }

    @Override
    public CompletableFuture<Page<GraduationProgram>> findAll(Integer pageNumber, Integer pageSize, String sortCriteriaJson, String searchCriteriaListJson) {
        final List<Sort.Order> sorts = new ArrayList<>();
        Specification<GraduationProgramEntity> gradProgramSpecs = gradProgramService.setSpecificationAndSortCriteria(sortCriteriaJson, searchCriteriaListJson, JsonUtil.mapper, sorts);
        return this.gradProgramService.findAll(gradProgramSpecs, pageNumber, pageSize, sorts).thenApplyAsync(gradProgramEntities -> gradProgramEntities.map(mapper::toStructure));
    }
}
