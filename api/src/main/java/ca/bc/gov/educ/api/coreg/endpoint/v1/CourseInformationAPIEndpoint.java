package ca.bc.gov.educ.api.coreg.endpoint.v1;

import ca.bc.gov.educ.api.coreg.constants.v1.URL;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import ca.bc.gov.educ.api.coreg.struct.v1.Courses;
import org.springframework.web.bind.annotation.*;
import ca.bc.gov.educ.api.coreg.constants.v1.PermissionsConstants;
import java.math.BigInteger;
import java.util.concurrent.CompletableFuture;

@RequestMapping(URL.BASE_URL_COURSE_INFORMATION)
@OpenAPIDefinition(info = @Info(title = "Courses",
        description = "This API is for Courses", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2")})
@Tag(name = "API for Course Information", description = "Operations related to Course Information")
public interface CourseInformationAPIEndpoint {

  @GetMapping("/{courseId}")
  @PreAuthorize(PermissionsConstants.READ_COREG_COURSE_DATA)
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional(readOnly = true)
  @Operation(summary = "Get Course Information by Id", description = "Fetch detailed information about a specific course using its unique identifier (Id)")
  @Schema(name = "COREG", implementation = Courses.class)
  Courses getCourseInformation(@PathVariable("courseId") BigInteger courseID);

  @GetMapping("/external/{externalCode}")
  @PreAuthorize(PermissionsConstants.READ_COREG_COURSE_DATA)
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional(readOnly = true)
  @Operation(summary = "Get Course Information by External Code", description = "Fetch detailed information about a specific course using its external code")
  @Schema(name = "COREG", implementation = Courses.class)
  Courses getCourseInformationByExternalCode(@PathVariable("externalCode")  String externalCode);

  @GetMapping("/paginated")
  @PreAuthorize(PermissionsConstants.READ_COREG_COURSE_DATA)
  @Async
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
  @Transactional(readOnly = true)
  @Operation(summary = "Search for Course Information", description = "Search for courses based on various criteria to retrieve relevant course details")
  CompletableFuture<Page<Courses>> findAll(@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                      @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                      @RequestParam(name = "sort", defaultValue = "") String sortCriteriaJson,
                                                      @RequestParam(name = "searchCriteriaList", required = false) String searchCriteriaListJson);

}
