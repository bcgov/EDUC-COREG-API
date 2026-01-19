package ca.bc.gov.educ.api.coreg.service;

import ca.bc.gov.educ.api.coreg.exception.CoregAPIRuntimeException;
import ca.bc.gov.educ.api.coreg.mapper.v1.CourseRegistryEventMapper;
import ca.bc.gov.educ.api.coreg.model.v1.*;
import ca.bc.gov.educ.api.coreg.repository.v1.*;
import ca.bc.gov.educ.api.coreg.service.v1.CourseRegistryEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRegistryEventServiceTest {

    @Mock
    private CourseStatusRepository courseStatusRepository;

    @Mock
    private CourseRegistryEventRepository courseRegistryEventRepository;

    @Mock
    private CoregCourseEventRepository coregCourseEventRepository;

    @Mock
    private CourseCodeMappingRepository courseCodeMappingRepository;

    @Mock
    private CourseRegistryEventMapper courseRegistryEventMapper;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CourseManagementRolesRepository courseManagementRolesRepository;

    @Mock
    private CourseAllowableCreditsRepository courseAllowableCreditsRepository;

    @Mock
    private GraduationProgramCourseRepository graduationProgramCourseRepository;

    @InjectMocks
    private CourseRegistryEventService service;

    @Test
    void getEventsFromPastDays_shouldThrowException_whenPastDaysLessThanOne() {
        assertThatThrownBy(() -> service.getEventsFromPastDays(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pastDays must be at least 1");
    }

    @Test
    void getEventsFromPastDays_shouldThrowException_whenPastDaysGreaterThan30() {
        assertThatThrownBy(() -> service.getEventsFromPastDays(31))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pastDays cannot be more than 30");
    }

    @Test
    void getEventsFromPastDays_shouldReturnMappedDtos() {
        // given
        CourseRegistryEventEntity entity = new CourseRegistryEventEntity();
        CourseRegistryEventDTO dto = new CourseRegistryEventDTO();
        List<CourseRegistryEventEntity> entities = List.of(entity);
        List<CourseRegistryEventDTO> dtos = List.of(dto);

        when(courseRegistryEventRepository.findByCreatedDateAfterOrderByCreatedDateAsc(
                any(LocalDateTime.class)))
                .thenReturn(entities);
        when(courseRegistryEventMapper.toDTOs(entities)).thenReturn(dtos);

        // when
        List<CourseRegistryEventDTO> result = service.getEventsFromPastDays(5);

        // then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(dto);
    }

    @Test
    void readCourseRegistryEvents_shouldSaveNewEvents_whenNotAlreadyExists() throws Exception {
        // given
        CourseRegistryEventDTO dto = new CourseRegistryEventDTO();
        dto.setId(12345L);
        dto.setRegistryEventTypeCharId(41L);
        dto.setAffectedTable("CRSE_COURSES");
        dto.setAffectedId(999L);

        CourseCodeEntity courseCodeEntity = new CourseCodeEntity();
        courseCodeEntity.setExternalCode("MATH 12");
        courseCodeEntity.setCrscdmapID(BigInteger.valueOf(1));

        CoregCourseEvent savedEvent = new CoregCourseEvent();

        when(courseRegistryEventRepository.findByCreatedDateAfterOrderByCreatedDateAsc(any(LocalDateTime.class)))
                .thenReturn(List.of(new CourseRegistryEventEntity()));
        when(courseRegistryEventMapper.toDTOs(any())).thenReturn(List.of(dto));
        when(coregCourseEventRepository.findFirstByCrsregevIdOrderByCreateDateDesc(dto.getId()))
                .thenReturn(Optional.empty());
        when(courseCodeMappingRepository.findByCoursesEntity_CourseIDAndOriginatingSystem(BigInteger.valueOf(999L), "39"))
                .thenReturn(Optional.of(courseCodeEntity));
        when(objectMapper.writeValueAsBytes(dto)).thenReturn("payload".getBytes());
        when(coregCourseEventRepository.save(any(CoregCourseEvent.class))).thenReturn(savedEvent);

        // when
        service.readCourseRegistryEvents();

        // then
        verify(coregCourseEventRepository, times(1)).save(any(CoregCourseEvent.class));
        verify(objectMapper, times(1)).writeValueAsBytes(dto);
    }

    @Test
    void readCourseRegistryEvents_shouldNotSave_whenEventAlreadyExists() {
        // given
        CourseRegistryEventDTO dto = new CourseRegistryEventDTO();
        dto.setId(12345L);
        dto.setRegistryEventTypeCharId(42L);
        dto.setAffectedTable("CRSE_COURSES");
        dto.setAffectedId(999L);

        when(courseRegistryEventRepository.findByCreatedDateAfterOrderByCreatedDateAsc(any(LocalDateTime.class)))
                .thenReturn(List.of(new CourseRegistryEventEntity()));
        when(courseRegistryEventMapper.toDTOs(any())).thenReturn(List.of(dto));
        when(coregCourseEventRepository.findFirstByCrsregevIdOrderByCreateDateDesc(dto.getId()))
                .thenReturn(Optional.of(new CoregCourseEvent()));

        // when
        service.readCourseRegistryEvents();

        // then
        verify(coregCourseEventRepository, never()).save(any());

        verify(courseCodeMappingRepository, never()).findByCoursesEntity_CourseIDAndOriginatingSystem(any(), any());
    }

    @Test
    void readCourseRegistryEvents_shouldThrowRuntimeException_whenJsonProcessingFails() throws Exception {
        // given
        CourseRegistryEventDTO dto = new CourseRegistryEventDTO();
        dto.setId(12345L);
        dto.setRegistryEventTypeCharId(43L);
        dto.setAffectedTable("CRSE_COURSE_STATUSES");
        dto.setAffectedId(999L);

        // Mock course status entity
        CourseStatusEntity courseStatusEntity = new CourseStatusEntity();
        CoursesEntity coursesEntity = new CoursesEntity();
        coursesEntity.setCourseID(BigInteger.valueOf(888L));
        courseStatusEntity.setCoursesEntity(coursesEntity);

        // Mock course code entity
        CourseCodeEntity courseCodeEntity = new CourseCodeEntity();
        courseCodeEntity.setExternalCode("SCI 10");

        when(courseRegistryEventRepository.findByCreatedDateAfterOrderByCreatedDateAsc(any(LocalDateTime.class)))
                .thenReturn(List.of(new CourseRegistryEventEntity()));
        when(courseRegistryEventMapper.toDTOs(any())).thenReturn(List.of(dto));
        when(coregCourseEventRepository.findFirstByCrsregevIdOrderByCreateDateDesc(dto.getId()))
                .thenReturn(Optional.empty());
        when(courseStatusRepository.findById(BigInteger.valueOf(999L)))
                .thenReturn(Optional.of(courseStatusEntity));
        when(courseCodeMappingRepository.findByCoursesEntity_CourseIDAndOriginatingSystem(BigInteger.valueOf(888L), "39"))
                .thenReturn(Optional.of(courseCodeEntity));
        when(objectMapper.writeValueAsBytes(any())).thenThrow(new JsonProcessingException("JSON error") {});

        // then
        assertThatThrownBy(() -> service.readCourseRegistryEvents())
                .isInstanceOf(CoregAPIRuntimeException.class)
                .hasMessageContaining("JSON error");

        verify(courseStatusRepository, times(1)).findById(BigInteger.valueOf(999L));
        verify(courseCodeMappingRepository, times(1)).findByCoursesEntity_CourseIDAndOriginatingSystem(any(), any());
        verify(coregCourseEventRepository, never()).save(any());
    }
}
