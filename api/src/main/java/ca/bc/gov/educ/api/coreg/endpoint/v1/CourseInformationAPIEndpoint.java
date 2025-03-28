package ca.bc.gov.educ.api.coreg.endpoint.v1;

import ca.bc.gov.educ.api.coreg.constants.v1.URL;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;
import ca.bc.gov.educ.api.coreg.struct.v1.Courses;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RequestMapping(URL.BASE_URL_COURSE_INFORMATION)
public interface CourseInformationAPIEndpoint {

  @GetMapping("/{courseId}")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional(readOnly = true)
  @Tag(name = "Course Information Entity", description = "Endpoints for course information.")
  @Schema(name = "COREG", implementation = Courses.class)
  Courses getCourseInformation(@PathVariable("courseId")  String courseID);

  @GetMapping("/external/{externalCode}")
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional(readOnly = true)
  @Tag(name = "Course Information Entity", description = "Endpoints for course information.")
  @Schema(name = "COREG", implementation = Courses.class)
  Courses getCourseInformationByExternalCode(@PathVariable("externalCode")  String externalCode);

  @GetMapping("/paginated")
  @Async
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  @Transactional(readOnly = true)
  @Tag(name = "Search Course Information", description = "Endpoints for course entity.")
  CompletableFuture<Page<Courses>> findAll(@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      @RequestParam(name = "sort", defaultValue = "") String sortCriteriaJson,
                                                      @RequestParam(name = "searchCriteriaList", required = false) String searchCriteriaListJson);

}
