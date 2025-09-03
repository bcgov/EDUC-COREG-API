package ca.bc.gov.educ.api.coreg.scheduler;

import ca.bc.gov.educ.api.coreg.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.coreg.service.v1.CourseRegistryEventService;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.org.lidalia.slf4jtest.TestLogger;
import uk.org.lidalia.slf4jtest.TestLoggerFactory;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.*;

class CoregCourseEventsSchedulerTest {

    private CourseRegistryEventService courseRegistryEventService;
    private CoregCourseEventsScheduler scheduler;
    private Publisher publisher;

    private final TestLogger log = TestLoggerFactory.getTestLogger(CoregCourseEventsScheduler.class);
    // ...
    private LogCaptor logCaptor;

    @Test
    void shouldLogMessage() {
        // Run the method that logs something
        scheduler.readCoregEvents();

        // Assert on logs
        assertThat(logCaptor.getLogs())
                .anyMatch(log -> log.contains("Running scheduled task"));
    }

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(CoregCourseEventsScheduler.class);
        courseRegistryEventService = mock(CourseRegistryEventService.class);
        scheduler = new CoregCourseEventsScheduler(courseRegistryEventService, publisher);
    }

    @Test
    void shouldCallReadCourseRegistryEvents() {
        // When
        scheduler.readCoregEvents();

        // Then
        verify(courseRegistryEventService, times(1)).readCourseRegistryEvents();
    }

    @Test
    void shouldHandleExceptionFromService() {
        doThrow(new RuntimeException("Simulated failure"))
                .when(courseRegistryEventService).readCourseRegistryEvents();

        scheduler.readCoregEvents(); // no exception should escape

        assertThat(logCaptor.getErrorLogs())
                .anyMatch(msg -> msg.contains("Error while reading course registry events"));
    }
}
