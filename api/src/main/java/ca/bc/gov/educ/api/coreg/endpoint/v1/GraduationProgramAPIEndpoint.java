package ca.bc.gov.educ.api.coreg.endpoint.v1;

import ca.bc.gov.educ.api.coreg.constants.v1.PermissionsConstants;
import ca.bc.gov.educ.api.coreg.constants.v1.URL;
import ca.bc.gov.educ.api.coreg.struct.v1.GraduationProgram;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.concurrent.CompletableFuture;

@RequestMapping(URL.BASE_URL_GRAD_PROGRAM_COURSE_INFORMATION)
@Tag(name = "API for Graduation Program", description = "Operations related to Graduation Program")
public interface GraduationProgramAPIEndpoint {

    @GetMapping("/paginated")
    @PreAuthorize(PermissionsConstants.READ_COREG_COURSE_DATA)
    @Async
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "OK"), @ApiResponse(responseCode = "500", description = "INTERNAL SERVER ERROR.")})
    @Transactional(readOnly = true)
    @Operation(summary = "Search Grad Program Courses", description = "Search for Grad Program Courses based on various criteria to retrieve relevant details")
    CompletableFuture<Page<GraduationProgram>> findAll(@RequestParam(name = "pageNumber", defaultValue = "0") Integer pageNumber,
                                                       @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
                                                       @RequestParam(name = "sort", defaultValue = "") String sortCriteriaJson,
                                                       @RequestParam(name = "searchCriteriaList", required = false) String searchCriteriaListJson);
}
