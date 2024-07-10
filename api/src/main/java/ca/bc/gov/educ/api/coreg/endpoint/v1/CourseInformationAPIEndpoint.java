package ca.bc.gov.educ.api.coreg.endpoint.v1;

import ca.bc.gov.educ.api.coreg.constants.v1.URL;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.transaction.annotation.Transactional;
import ca.bc.gov.educ.api.coreg.struct.v1.Courses;
import org.springframework.web.bind.annotation.*;

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

}
