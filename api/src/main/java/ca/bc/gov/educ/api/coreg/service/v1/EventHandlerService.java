package ca.bc.gov.educ.api.coreg.service.v1;

import static lombok.AccessLevel.PRIVATE;

import ca.bc.gov.educ.api.coreg.mapper.v1.CourseInformationMapper;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseCodeMappingRepository;
import ca.bc.gov.educ.api.coreg.struct.v1.Event;
import ca.bc.gov.educ.api.coreg.util.JsonUtilWithJavaTime;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


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
    private final CourseCodeMappingRepository courseCodeMappingRepository;

    @Getter(PRIVATE)
    private final CourseInformationService courseInformationService;
    private final CourseInformationMapper courseInformationMapper;

    /**
     * Instantiates a new Event handler service.
     *
     */
    @Autowired
    public EventHandlerService(CourseCodeMappingRepository courseCodeMappingRepository, CourseInformationService courseInformationService) {
        this.courseCodeMappingRepository = courseCodeMappingRepository;
        this.courseInformationService = courseInformationService;
        this.courseInformationMapper = Mappers.getMapper(CourseInformationMapper.class);
    }

    /**
     * Saga should never be null for this type of event.
     * this method expects that the event payload contains a pen number.
     *
     * @param event         containing the Course External ID.
     * @return the byte [ ]
     * @throws JsonProcessingException the json processing exception
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public byte[] handleGetCourseFromExternalIDEvent(Event event) throws JsonProcessingException {
        // always syncronous
        val optionalCourseCodeEntity = courseCodeMappingRepository.findByExternalCode(event.getEventPayload());
        log.debug("Optional course code entity present? " + !optionalCourseCodeEntity.isEmpty());
        if (!optionalCourseCodeEntity.isEmpty()) {
            var course = courseInformationMapper.toStructure(optionalCourseCodeEntity.get(0).getCoursesEntity());
            log.debug("Returning " + course);
            return JsonUtilWithJavaTime.getJsonBytesFromObject(course);
        } else {
            return new byte[0];
        }

    }
}
