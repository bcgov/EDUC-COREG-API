package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.constants.v1.ActivityCode;
import ca.bc.gov.educ.api.coreg.constants.v1.EventOutcome;
import ca.bc.gov.educ.api.coreg.constants.v1.EventStatus;
import ca.bc.gov.educ.api.coreg.constants.v1.EventType;
import ca.bc.gov.educ.api.coreg.mapper.v1.CourseRegistryEventMapper;
import ca.bc.gov.educ.api.coreg.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.coreg.model.v1.CoregCourseEvent;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.coreg.repository.v1.CoregCourseEventRepository;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseCodeMappingRepository;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseRegistryEventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseRegistryEventService {

    private final CourseRegistryEventRepository courseRegistryEventRepository;
    private final CoregCourseEventRepository coregCourseEventRepository;
    private final CourseCodeMappingRepository courseCodeMappingRepository;
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
                .findByCreatedDateAfter(fromDate));
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
                    BigInteger courseID = toUnsignedBigInteger(courseRegistryEvent.getId());
                    var course = courseCodeMappingRepository.findByCoursesEntity_CourseIDAndOriginatingSystem(courseID,"39");
                    if(course.isPresent()){
                        String courseCode = null;
                        String courseLevel = null;
                        var code = course.get().getExternalCode();
                        if(StringUtils.isNotBlank(code) && code.length() < 6) {
                            courseCode = code;
                        }else if(StringUtils.isNotBlank(code) && code.length() > 5) {
                            courseCode = code.substring(0, 4);
                            courseLevel = code.substring(5);
                        }
                        courseRegistryEvent.setCourseCode(courseCode);
                        courseRegistryEvent.setCourseLevel(courseLevel); 
                    }
 
                    coregCourseEvent = CoregCourseEvent.builder()
                            .crsregevId(courseRegistryEvent.getId())
                            .eventPayload(objectMapper.writeValueAsBytes(courseRegistryEvent)) // will be stored as bytes
                            .eventStatus(EventStatus.DB_COMMITTED.name())
                            .eventType(EventType.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name())
                            .createUser("COREG-SCHEDULER")
                            .updateUser("COREG-SCHEDULER")
                            .eventOutcome(EventOutcome.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name())
                            .activityCode(ActivityCode.COREG_EVENT.name())
                            .build();
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
                // save them in COREG_COURSE_EVENT table if the record doesn't exist
                coregCourseEventRepository.save(coregCourseEvent);
            }
        });
    }

    private static BigInteger toUnsignedBigInteger(long i) {
        if (i >= 0L)
            return BigInteger.valueOf(i);
        else {
            int upper = (int) (i >>> 32);
            int lower = (int) i;

            // return (upper << 32) + lower
            return (BigInteger.valueOf(Integer.toUnsignedLong(upper))).shiftLeft(32).
                    add(BigInteger.valueOf(Integer.toUnsignedLong(lower)));
        }
    }
}
