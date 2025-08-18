package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.mapper.v1.CourseRegistryEventMapper;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseRegistryEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseRegistryEventService {

    private final CourseRegistryEventRepository repository;
    private final CourseRegistryEventMapper mapper;

    public List<CourseRegistryEventDTO> getEventsFromPastDays(int pastDays) {
        if (pastDays < 1) {
            throw new IllegalArgumentException("pastDays must be at least 1");
        }
        if (pastDays > 30) {
            throw new IllegalArgumentException("pastDays cannot be more than 30");
        }

        LocalDateTime fromDate = LocalDateTime.now().minusDays(pastDays);
        return mapper.toCourseRegistryEventDTOs(repository.findByCreatedDateAfter(fromDate));
    }
}

