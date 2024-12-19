package ca.bc.gov.educ.api.coreg.service.v1;

import static ca.bc.gov.educ.api.coreg.constants.v1.EventStatus.MESSAGE_PUBLISHED;
import static lombok.AccessLevel.PRIVATE;

import ca.bc.gov.educ.api.coreg.constants.v1.EventOutcome;
import ca.bc.gov.educ.api.coreg.constants.v1.EventType;
import ca.bc.gov.educ.api.coreg.mapper.v1.CourseInformationMapper;
import ca.bc.gov.educ.api.coreg.model.v1.CoregStatusEvent;
import ca.bc.gov.educ.api.coreg.repository.v1.CoregStatusEventRepository;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseCodeMappingRepository;
import ca.bc.gov.educ.api.coreg.struct.v1.Courses;
import ca.bc.gov.educ.api.coreg.struct.v1.Event;
import ca.bc.gov.educ.api.coreg.util.JsonUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * The type Event handler service.
 */
@Service
@Slf4j
@SuppressWarnings("java:S3864")
public class EventHandlerService {

    /**
     * The constant NO_RECORD_SAGA_ID_EVENT_TYPE.
     */
    public static final String NO_RECORD_SAGA_ID_EVENT_TYPE = "no record found for the saga id and event type combination, processing.";
    /**
     * The constant RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE.
     */
    public static final String RECORD_FOUND_FOR_SAGA_ID_EVENT_TYPE = "record found for the saga id and event type combination, might be a duplicate or replay," +
            " just updating the db status so that it will be polled and sent back again.";
    /**
     * The constant PAYLOAD_LOG.
     */
    public static final String PAYLOAD_LOG = "payload is :: {}";
    /**
     * The constant EVENT_PAYLOAD.
     */
    public static final String EVENT_PAYLOAD = "event is :: {}";

    @Getter(PRIVATE)
    private final CoregStatusEventRepository coregStatusEventRepository;

    @Getter(PRIVATE)
    private final CourseCodeMappingRepository courseCodeMappingRepository;

    @Getter(PRIVATE)
    private final CourseInformationService courseInformationService;
    private final CourseInformationMapper courseInformationMapper;

    /**
     * Instantiates a new Event handler service.
     *
     * @param coregStatusEventRepository the student event repository
     */
    @Autowired
    public EventHandlerService(final CoregStatusEventRepository coregStatusEventRepository, CourseCodeMappingRepository courseCodeMappingRepository, CourseInformationService courseInformationService, CourseInformationMapper courseInformationMapper) {
        this.coregStatusEventRepository = coregStatusEventRepository;
        this.courseCodeMappingRepository = courseCodeMappingRepository;
        this.courseInformationService = courseInformationService;
        this.courseInformationMapper = courseInformationMapper;
    }

    /**
     * Saga should never be null for this type of event.
     * this method expects that the event payload contains a pen number.
     *
     * @param event         containing the student PEN.
     * @param isSynchronous the is synchronous
     * @return the byte [ ]
     * @throws JsonProcessingException the json processing exception
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public byte[] handleGetCourseFromExternalIDEvent(Event event, boolean isSynchronous) throws JsonProcessingException {
        if (isSynchronous) {
            val optionalCourseCodeEntity = courseCodeMappingRepository.findByExternalCode(event.getEventPayload());
            if (optionalCourseCodeEntity.isPresent()) {

                return JsonUtil.getJsonBytesFromObject(courseInformationMapper.toStructure(optionalCourseCodeEntity.get().getCoursesEntity()));
            } else {
                return new byte[0];
            }
        }

        log.trace(EVENT_PAYLOAD, event);
        val optionalCourseCodeEntity =  courseCodeMappingRepository.findByExternalCode(event.getEventPayload());
        if (optionalCourseCodeEntity.isPresent()) {
            Courses courses = courseInformationMapper.toStructure(optionalCourseCodeEntity.get().getCoursesEntity()); // need to convert to structure MANDATORY otherwise jackson will break.
            event.setEventPayload(JsonUtil.getJsonStringFromObject(courses));
            event.setEventOutcome(EventOutcome.COURSE_FOUND);
        } else {
            event.setEventOutcome(EventOutcome.COURSE_NOT_FOUND);
        }
        val coregStatusEvent = createCoregStatusEventRecord(event);
        return createResponseEvent(coregStatusEvent);
    }

    private CoregStatusEvent createCoregStatusEventRecord(Event event) {
        return CoregStatusEvent.builder()
                .createDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .createUser(event.getEventType().toString())
                .updateUser(event.getEventType().toString())
                .eventPayloadBytes(event.getEventPayload().getBytes())
                .eventType(event.getEventType().toString())
                .sagaId(event.getSagaId())
                .eventStatus(MESSAGE_PUBLISHED.toString())
                .eventOutcome(event.getEventOutcome().toString())
                .replyChannel(event.getReplyTo())
                .build();
    }

    private byte[] createResponseEvent(CoregStatusEvent event) throws JsonProcessingException {
        val responseEvent = Event.builder()
                .sagaId(event.getSagaId())
                .eventType(EventType.valueOf(event.getEventType()))
                .eventOutcome(EventOutcome.valueOf(event.getEventOutcome()))
                .eventPayload(event.getEventPayload()).build();
        return JsonUtil.getJsonBytesFromObject(responseEvent);
    }
}
