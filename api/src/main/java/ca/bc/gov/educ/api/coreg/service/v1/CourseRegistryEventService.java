package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.constants.v1.ActivityCode;
import ca.bc.gov.educ.api.coreg.constants.v1.EventOutcome;
import ca.bc.gov.educ.api.coreg.constants.v1.EventStatus;
import ca.bc.gov.educ.api.coreg.constants.v1.EventType;
import ca.bc.gov.educ.api.coreg.mapper.v1.CourseRegistryEventMapper;
import ca.bc.gov.educ.api.coreg.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.coreg.model.v1.CoregCourseEvent;
import ca.bc.gov.educ.api.coreg.model.v1.CoregStatusEvent;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.coreg.repository.v1.CoregCourseEventRepository;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseRegistryEventRepository;
import ca.bc.gov.educ.api.coreg.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseRegistryEventService {

    private final CourseRegistryEventRepository courseRegistryEventRepository;
    private final CoregCourseEventRepository coregCourseEventRepository;
    private final CourseRegistryEventMapper courseRegistryEventMapper;
    private final Publisher publisher;
    private final ObjectMapper objectMapper;

    public List<CourseRegistryEventDTO> getEventsFromPastDays(int pastDays) {
        if (pastDays < 1) {
            throw new IllegalArgumentException("pastDays must be at least 1");
        }
        if (pastDays > 30) {
            throw new IllegalArgumentException("pastDays cannot be more than 30");
        }

        LocalDateTime fromDate = LocalDateTime.now().minusDays(pastDays);
        return courseRegistryEventMapper.toDTOs(courseRegistryEventRepository
                .findByAffectedTableAndCreatedDateAfter("CRSE_COURSES", fromDate));
    }

    public void readCourseRegistryEvents() {
        //Get Course registry events
        List<CourseRegistryEventDTO> courseRegistryEvents = getEventsFromPastDays(30);

        courseRegistryEvents.forEach(courseRegistryEvent -> {
            log.debug("Event type: " + EventType.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name());
            // Check if exists in the table
            Optional<CoregCourseEvent> existingEvent = coregCourseEventRepository.findFirstByCrsregevIdOrderByCreateDateDesc(courseRegistryEvent.getId());
            CoregCourseEvent coregCourseEvent = null;
            if (existingEvent.isEmpty()) {
                try {
                    coregCourseEvent = CoregCourseEvent.builder()
                            .crsregevId(courseRegistryEvent.getId())
                            .eventPayload(objectMapper.writeValueAsBytes(courseRegistryEvent)) // will be stored as bytes
                            .eventStatus(EventStatus.DB_COMMITTED.name())
                            .eventType(EventType.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name())
                            .createUser("COREG-SCHEDULER")
                            .eventOutcome(EventOutcome.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name())
                            .build();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                // save them in COREG_COURSE_EVENTS table if the record doesn't exist
                coregCourseEventRepository.save(coregCourseEvent);
                /*publisher.dispatchChoreographyEvent(CoregStatusEvent.builder()
                                .eventPayloadBytes("Payload here".getBytes())
                                .eventType(EventType.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name())
                                .eventStatus(EventStatus.MESSAGE_PUBLISHED.name())
                                .eventOutcome(EventType.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name())
                                .activityCode(ActivityCode.COREG_EVENT.name())
                                .build());*/
            }
        });
    }
}
