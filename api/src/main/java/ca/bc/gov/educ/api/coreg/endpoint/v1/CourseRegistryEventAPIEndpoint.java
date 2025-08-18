package ca.bc.gov.educ.api.coreg.endpoint.v1;

import ca.bc.gov.educ.api.coreg.constants.v1.PermissionsConstants;
import ca.bc.gov.educ.api.coreg.constants.v1.URL;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping(URL.BASE_URL_COURSE)
@OpenAPIDefinition(info = @Info(title = "Course Registry Events",
        description = "This API is for Course registry events", version = "1"),
        security = {@SecurityRequirement(name = "OAUTH2")})
@Tag(name = "API for Course Registry Events", description = "Operations related to Course Registry Events")
public interface CourseRegistryEventAPIEndpoint {

  @GetMapping("/registry-events")
  @PreAuthorize(PermissionsConstants.READ_COREG_COURSE_DATA)
  @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "404", description = "NOT FOUND")})
  @Transactional(readOnly = true)
  @Operation(summary = "Get Course Registry Events", description = "Fetch detailed information about Course Registry Events")
  @Schema(name = "COREG", implementation = CourseRegistryEventDTO.class)

  List<CourseRegistryEventDTO> getEventsFromPastDays(
          @RequestParam(defaultValue = "5")
          @Min(value = 1, message = "pastDays must be at least 1")
          @Max(value = 30, message = "pastDays cannot be more than 30")
          int pastDays
  );

}
