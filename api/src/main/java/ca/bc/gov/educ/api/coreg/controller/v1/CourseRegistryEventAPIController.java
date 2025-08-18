package ca.bc.gov.educ.api.coreg.controller.v1;

import ca.bc.gov.educ.api.coreg.endpoint.v1.CourseRegistryEventAPIEndpoint;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.coreg.service.v1.CourseRegistryEventService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class CourseRegistryEventAPIController implements CourseRegistryEventAPIEndpoint {

    private final CourseRegistryEventService service;

    public List<CourseRegistryEventDTO> getEventsFromPastDays(
            @RequestParam(defaultValue = "5")
            @Min(value = 1, message = "pastDays must be at least 1")
            @Max(value = 30, message = "pastDays cannot be more than 30")
            int pastDays
    ) {
        return service.getEventsFromPastDays(pastDays);
    }
}

