package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.constants.v1.ActivityCode;
import ca.bc.gov.educ.api.coreg.constants.v1.EventOutcome;
import ca.bc.gov.educ.api.coreg.constants.v1.EventStatus;
import ca.bc.gov.educ.api.coreg.constants.v1.EventType;
import ca.bc.gov.educ.api.coreg.exception.CoregAPIRuntimeException;
import ca.bc.gov.educ.api.coreg.exception.EntityNotFoundException;
import ca.bc.gov.educ.api.coreg.mapper.v1.CourseRegistryEventMapper;
import ca.bc.gov.educ.api.coreg.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.coreg.model.v1.*;
import ca.bc.gov.educ.api.coreg.repository.v1.*;
import ca.bc.gov.educ.api.coreg.struct.v1.Event;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseRegistryEventService {

    private final CourseStatusRepository courseStatusRepository;
    private final CourseRegistryEventRepository courseRegistryEventRepository;
    private final CoregCourseEventRepository coregCourseEventRepository;
    private final CourseCodeMappingRepository courseCodeMappingRepository;
    private final CourseRegistryEventMapper courseRegistryEventMapper;
    private final ObjectMapper objectMapper;
    private final CourseManagementRolesRepository courseManagementRolesRepository;
    private final CourseAllowableCreditsRepository courseAllowableCreditsRepository;
    private final GraduationProgramCourseRepository graduationProgramCourseRepository;
    private final Publisher publisher;

    public List<CourseRegistryEventDTO> getEventsFromPastDays(int pastDays) {
        if (pastDays < 1) {
            throw new IllegalArgumentException("pastDays must be at least 1");
        }
        if (pastDays > 30) {
            throw new IllegalArgumentException("pastDays cannot be more than 30");
        }

        LocalDateTime fromDate = LocalDateTime.now().minusDays(pastDays);
        return courseRegistryEventMapper.toDTOs(courseRegistryEventRepository
                .findByCreatedDateAfterOrderByCreatedDateAsc(fromDate));
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public List<CoregCourseEvent> readCourseRegistryEvents() {
        //Get Course registry events
        List<CourseRegistryEventDTO> courseRegistryEvents = getEventsFromPastDays(30);
        
        List<CoregCourseEvent> events = new ArrayList<>();

        courseRegistryEvents.forEach(courseRegistryEvent -> {
            log.debug("Event type: " + EventType.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name());
            
            // Check if exists in the table
            Optional<CoregCourseEvent> existingEvent = coregCourseEventRepository.findFirstByCrsregevIdOrderByCreateDateDesc(courseRegistryEvent.getId());
            if (existingEvent.isEmpty()) {
                switch(courseRegistryEvent.getAffectedTable()) {
                    case "CRSE_COURSE_STATUSES":
                        setValuesForCourseStatusChange(courseRegistryEvent, events);
                        break;
                    case "CRSE_COURSE_MANAGEMENT_ROLES":
                        setValuesForCourseManagementRoleChange(courseRegistryEvent, events);
                        break;
                    case "CRSE_GRADUATION_PROGRAM_COURSS":
                        setValuesForCourseGradProgamCourseChange(courseRegistryEvent, events);
                        break;
                    case "CRSE_COURSE_ALLOWABLE_CREDITS":
                        setValuesForCourseAllowableCreditsChange(courseRegistryEvent, events);
                        break;
                    case "CRSE_COURSES":
                        setValuesForCourseChange(courseRegistryEvent, events);
                        break;
                    case "CRSE_COURSE_CODE_MAPPINGS":
                        setValuesForCourseCodeMappingChange(courseRegistryEvent, events);
                        break;
                    default:
                        //Do nothing
                }
            }
        });
        
        return events;
    }

    private void setValuesForCourseGradProgamCourseChange(CourseRegistryEventDTO courseRegistryEvent, List<CoregCourseEvent> events){
        BigInteger courseGradProgramID = toUnsignedBigInteger(courseRegistryEvent.getAffectedId());
        var gradProgram = graduationProgramCourseRepository.findById(courseGradProgramID);
        if(gradProgram.isPresent()) {
            var course = courseCodeMappingRepository.findByCoursesEntity_CourseIDAndOriginatingSystem(gradProgram.get().getCourseID(),"39");
            setFinalCourseCodeAndLevelValues(course, courseRegistryEvent, events);
        }
    }

    private void setValuesForCourseManagementRoleChange(CourseRegistryEventDTO courseRegistryEvent, List<CoregCourseEvent> events){
        BigInteger courseManagementRoleID = toUnsignedBigInteger(courseRegistryEvent.getAffectedId());
        var courseManagementRole = courseManagementRolesRepository.findById(courseManagementRoleID);
        if(courseManagementRole.isPresent()) {
            var course = courseCodeMappingRepository.findByCoursesEntity_CourseIDAndOriginatingSystem(courseManagementRole.get().getCoursesEntity().getCourseID(), "39");
            setFinalCourseCodeAndLevelValues(course, courseRegistryEvent, events);
        }
    }

    private void setValuesForCourseAllowableCreditsChange(CourseRegistryEventDTO courseRegistryEvent, List<CoregCourseEvent> events){
        BigInteger courseAllowableCreditID = toUnsignedBigInteger(courseRegistryEvent.getAffectedId());
        var courseAllowableCredits = courseAllowableCreditsRepository.findById(courseAllowableCreditID);
        if(courseAllowableCredits.isPresent()) {
            var course = courseCodeMappingRepository.findByCoursesEntity_CourseIDAndOriginatingSystem(courseAllowableCredits.get().getCoursesEntity().getCourseID(), "39");
            setFinalCourseCodeAndLevelValues(course, courseRegistryEvent, events);
        }
    }

    private void setValuesForCourseCodeMappingChange(CourseRegistryEventDTO courseRegistryEvent, List<CoregCourseEvent> events){
        BigInteger courseCodeMappingID = toUnsignedBigInteger(courseRegistryEvent.getAffectedId());
        var course = courseCodeMappingRepository.findById(courseCodeMappingID);
        setFinalCourseCodeAndLevelValues(course, courseRegistryEvent, events);
    }
    
    private void setValuesForCourseStatusChange(CourseRegistryEventDTO courseRegistryEvent, List<CoregCourseEvent> events){
        BigInteger courseStatusID = toUnsignedBigInteger(courseRegistryEvent.getAffectedId());
        var courseStatus = courseStatusRepository.findById(courseStatusID);
        if(courseStatus.isPresent()) {
            var course = courseCodeMappingRepository.findByCoursesEntity_CourseIDAndOriginatingSystem(courseStatus.get().getCoursesEntity().getCourseID(), "39");
            setFinalCourseCodeAndLevelValues(course, courseRegistryEvent, events);
        }
    }

    private void setValuesForCourseChange(CourseRegistryEventDTO courseRegistryEvent, List<CoregCourseEvent> events){
        BigInteger courseID = toUnsignedBigInteger(courseRegistryEvent.getAffectedId());
        var course = courseCodeMappingRepository.findByCoursesEntity_CourseIDAndOriginatingSystem(courseID,"39");
        setFinalCourseCodeAndLevelValues(course, courseRegistryEvent, events);
    }

    private void createAndSendEvent(CourseRegistryEventDTO courseRegistryEvent, List<CoregCourseEvent> events) {
        try {
            var coregCourseEvent = CoregCourseEvent.builder()
                    .crsregevId(courseRegistryEvent.getId())
                    .eventPayload(objectMapper.writeValueAsBytes(courseRegistryEvent)) // will be stored as bytes
                    .eventStatus(EventStatus.DB_COMMITTED.name())
                    .eventType(EventType.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name())
                    .createUser("COREG-SCHEDULER")
                    .updateUser("COREG-SCHEDULER")
                    .eventOutcome(EventOutcome.fromCode(courseRegistryEvent.getRegistryEventTypeCharId()).name())
                    .activityCode(ActivityCode.COREG_EVENT.name())
                    .build();
            
            events.add(coregCourseEventRepository.save(coregCourseEvent));
        } catch (JsonProcessingException e) {
            throw new CoregAPIRuntimeException(e.getMessage());
        }
    }
    
    private void setFinalCourseCodeAndLevelValues(Optional<CourseCodeEntity> course, CourseRegistryEventDTO courseRegistryEvent, List<CoregCourseEvent> events){
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
            createAndSendEvent(courseRegistryEvent, events);
        }
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
