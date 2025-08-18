package ca.bc.gov.educ.api.coreg.service;

import ca.bc.gov.educ.api.coreg.mapper.v1.CourseRegistryEventMapper;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventEntity;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseRegistryEventRepository;
import ca.bc.gov.educ.api.coreg.service.v1.CourseRegistryEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class CourseRegistryEventServiceTest {

    @Mock
    private CourseRegistryEventRepository repository;

    @Mock
    private CourseRegistryEventMapper mapper;

    @InjectMocks
    private CourseRegistryEventService service;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEventsFromPastDays_shouldReturnMappedDTOs() {
        int pastDays = 7;

        CourseRegistryEventEntity entity = new CourseRegistryEventEntity();
        CourseRegistryEventDTO dto = new CourseRegistryEventDTO();

        when(repository.findByCreatedDateAfter(any(LocalDateTime.class)))
                .thenReturn(List.of(entity));
        when(mapper.toCourseRegistryEventDTOs(List.of(entity)))
                .thenReturn(List.of(dto));

        List<CourseRegistryEventDTO> result = service.getEventsFromPastDays(pastDays);

        assertThat(result).hasSize(1).contains(dto);
        verify(repository, times(1)).findByCreatedDateAfter(any(LocalDateTime.class));
        verify(mapper, times(1)).toCourseRegistryEventDTOs(List.of(entity));
    }

    @Test
    void getEventsFromPastDays_shouldReturnEmptyList_whenNoEventsFound() {
        when(repository.findByCreatedDateAfter(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());
        when(mapper.toCourseRegistryEventDTOs(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        List<CourseRegistryEventDTO> result = service.getEventsFromPastDays(10);

        assertThat(result).isEmpty();
        verify(repository, times(1)).findByCreatedDateAfter(any(LocalDateTime.class));
        verify(mapper, times(1)).toCourseRegistryEventDTOs(Collections.emptyList());
    }

    @Test
    void getEventsFromPastDays_shouldThrowException_whenPastDaysBelowMin() {
        assertThatThrownBy(() -> service.getEventsFromPastDays(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pastDays must be at least 1");
    }

    @Test
    void getEventsFromPastDays_shouldThrowException_whenPastDaysAboveMax() {
        assertThatThrownBy(() -> service.getEventsFromPastDays(31))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("pastDays cannot be more than 30");
    }
}
