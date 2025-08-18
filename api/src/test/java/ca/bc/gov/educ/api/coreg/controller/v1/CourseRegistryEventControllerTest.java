package ca.bc.gov.educ.api.coreg.controller.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.coreg.service.v1.CourseRegistryEventService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.javacrumbs.shedlock.core.LockProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = CourseRegistryEventAPIController.class)
@AutoConfigureMockMvc(addFilters = false)
class CourseRegistryEventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CourseRegistryEventService service;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LockProvider lockProvider;

    @Test
    void shouldReturnEvents_withDefaultPastDays() throws Exception {
        // given
        CourseRegistryEventDTO dto = CourseRegistryEventDTO.builder()
                .id(1L)
                .affectedTable("COURSE")
                .affectedId(100L)
                .registryEventTypeCharId(200L)
                .createdDate(LocalDateTime.now())
                .createdUser("testUser")
                .dataSources(Collections.emptyList())
                .build();

        when(service.getEventsFromPastDays(5)).thenReturn(List.of(dto));

        // when
        String response = mockMvc.perform(get("/api/v1/course/registry-events"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // then
        List<CourseRegistryEventDTO> result =
                objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAffectedTable()).isEqualTo("COURSE");

        verify(service).getEventsFromPastDays(5);
    }


    @Test
    void shouldReturnEvents_withCustomPastDays() throws Exception {
        CourseRegistryEventDTO dto = CourseRegistryEventDTO.builder()
                .id(2L)
                .affectedTable("STUDENT")
                .affectedId(111L)
                .registryEventTypeCharId(222L)
                .createdDate(LocalDateTime.now())
                .createdUser("anotherUser")
                .dataSources(Collections.emptyList())
                .build();

        when(service.getEventsFromPastDays(10)).thenReturn(List.of(dto));

        String response = mockMvc.perform(get("/api/v1/course/registry-events")
                        .param("pastDays", "10"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        List<CourseRegistryEventDTO> result =
                objectMapper.readValue(response, new TypeReference<>() {});
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAffectedTable()).isEqualTo("STUDENT");

        verify(service).getEventsFromPastDays(10);
    }


    @Test
    void shouldFailValidation_whenPastDaysBelowMin() throws Exception {
        mockMvc.perform(get("/api/v1/course/registry-events")
                        .param("pastDays", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.0").value("pastDays must be at least 1"));
    }

    @Test
    void shouldFailValidation_whenPastDaysAboveMax() throws Exception {
        mockMvc.perform(get("/api/v1/course/registry-events")
                        .param("pastDays", "31"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.31").value("pastDays cannot be more than 30"));
    }

}
