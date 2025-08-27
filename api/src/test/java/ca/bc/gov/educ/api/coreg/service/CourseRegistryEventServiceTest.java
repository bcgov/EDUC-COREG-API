package ca.bc.gov.educ.api.coreg.service;

import ca.bc.gov.educ.api.coreg.mapper.v1.CourseRegistryEventMapper;
import ca.bc.gov.educ.api.coreg.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.coreg.model.v1.CoregCourseEvent;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventEntity;
import ca.bc.gov.educ.api.coreg.repository.v1.CoregCourseEventRepository;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseRegistryEventRepository;
import ca.bc.gov.educ.api.coreg.service.v1.CourseRegistryEventService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CourseRegistryEventServiceTest {

    @Mock
    private CourseRegistryEventRepository courseRegistryEventRepository;

    @Mock
    private CoregCourseEventRepository coregCourseEventRepository;

    @Mock
    private CourseRegistryEventMapper courseRegistryEventMapper;

    @Mock
    private Publisher publisher;

    @Mock
    private ObjectMapper objectMapper;

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

        when(courseRegistryEventRepository.findByCreatedDateAfter(
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

        when(courseRegistryEventRepository.findByCreatedDateAfter(
                any(LocalDateTime.class)))
                .thenReturn(List.of(new CourseRegistryEventEntity()));

        when(courseRegistryEventMapper.toDTOs(any())).thenReturn(List.of(dto));
        when(coregCourseEventRepository.findFirstByCrsregevIdOrderByCreateDateDesc(dto.getId()))
                .thenReturn(Optional.empty());
        when(objectMapper.writeValueAsBytes(dto)).thenReturn("payload".getBytes());

        // when
        service.readCourseRegistryEvents();

        // then
        verify(coregCourseEventRepository, times(1)).save(any(CoregCourseEvent.class));
    }

    @Test
    void readCourseRegistryEvents_shouldNotSave_whenEventAlreadyExists() {
        // given
        CourseRegistryEventDTO dto = new CourseRegistryEventDTO();
        dto.setId(12345L);
        dto.setRegistryEventTypeCharId(42L);

        when(courseRegistryEventRepository.findByCreatedDateAfter(
                any(LocalDateTime.class)))
                .thenReturn(List.of(new CourseRegistryEventEntity()));

        when(courseRegistryEventMapper.toDTOs(any())).thenReturn(List.of(dto));
        when(coregCourseEventRepository.findFirstByCrsregevIdOrderByCreateDateDesc(dto.getId()))
                .thenReturn(Optional.of(new CoregCourseEvent()));

        // when
        service.readCourseRegistryEvents();

        // then
        verify(coregCourseEventRepository, never()).save(any());
    }

    @Test
    void readCourseRegistryEvents_shouldThrowRuntimeException_whenJsonProcessingFails() throws Exception {
        // given
        CourseRegistryEventDTO dto = new CourseRegistryEventDTO();
        dto.setId(12345L);
        dto.setRegistryEventTypeCharId(43L);

        when(courseRegistryEventRepository.findByCreatedDateAfter(
                any(LocalDateTime.class)))
                .thenReturn(List.of(new CourseRegistryEventEntity()));

        when(courseRegistryEventMapper.toDTOs(any())).thenReturn(List.of(dto));
        when(coregCourseEventRepository.findFirstByCrsregevIdOrderByCreateDateDesc(dto.getId()))
                .thenReturn(Optional.empty());
        when(objectMapper.writeValueAsBytes(dto)).thenThrow(JsonProcessingException.class);

        // then
        assertThatThrownBy(() -> service.readCourseRegistryEvents())
                .isInstanceOf(RuntimeException.class);
    }
}
