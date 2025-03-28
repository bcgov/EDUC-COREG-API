package ca.bc.gov.educ.api.coreg.controller.v1;

import ca.bc.gov.educ.api.coreg.endpoint.v1.CourseInformationAPIEndpoint;
import ca.bc.gov.educ.api.coreg.mapper.v1.CourseInformationMapper;
import ca.bc.gov.educ.api.coreg.service.v1.CourseInformationService;
import ca.bc.gov.educ.api.coreg.struct.v1.Courses;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RestController;
import java.util.concurrent.CompletableFuture;

@RestController
@Slf4j
@AllArgsConstructor
public class CourseInformationAPIController implements CourseInformationAPIEndpoint {

  private static final CourseInformationMapper mapper = CourseInformationMapper.mapper;
  private final CourseInformationService courseInformationService;

  @Override
  public Courses getCourseInformation(String courseID) {
    return mapper.toStructure(courseInformationService.getCourseInformation(courseID));
  }

  @Override
  public Courses getCourseInformationByExternalCode(String externalCode) {
    return mapper.toStructure(courseInformationService.getCourseInformationByExternalCode(externalCode));
  }

  @Override
  public CompletableFuture<Page<Courses>> findAll(Integer pageNumber, Integer pageSize, String sortCriteriaJson, String searchCriteriaListJson) {
    return courseInformationService.getCourseInformationByCriteria(pageNumber, pageSize, sortCriteriaJson, searchCriteriaListJson).thenApplyAsync(coursesLightEntities -> coursesLightEntities.map(mapper::toStructure));
  }

}

