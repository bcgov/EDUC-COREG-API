package ca.bc.gov.educ.api.coreg.scheduler;

import ca.bc.gov.educ.api.coreg.constants.v1.EventStatus;
import ca.bc.gov.educ.api.coreg.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.coreg.model.v1.CoregCourseEvent;
import ca.bc.gov.educ.api.coreg.repository.v1.CoregCourseEventRepository;
import ca.bc.gov.educ.api.coreg.properties.ApplicationProperties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static ca.bc.gov.educ.api.coreg.constants.v1.EventStatus.DB_COMMITTED;
import static org.mockito.Mockito.*;

import net.javacrumbs.shedlock.core.LockAssert;
import org.mockito.MockedStatic;

@ExtendWith(MockitoExtension.class)
class JetStreamEventSchedulerTest {

    @Mock
    CoregCourseEventRepository coregCourseEventRepository;

    @Mock
    Publisher publisher;

    @Mock
    ApplicationProperties applicationProperties;

    @InjectMocks
    private JetStreamEventScheduler scheduler;


    private MockedStatic<LockAssert> lockAssertStatic;

    @BeforeEach
    void setup() {
        lockAssertStatic = mockStatic(LockAssert.class);

        // Proper way to stub a void static method
        lockAssertStatic.when(() -> LockAssert.assertLocked()).thenAnswer(invocation -> null);
    }

    @AfterEach
    void cleanup() {
        if (lockAssertStatic != null) {
            lockAssertStatic.close();
        }
    }


    @Test
    void shouldPublishEventsAndUpdateStatus() {
        LockAssert.assertLocked();
        // Arrange
        CoregCourseEvent eventEntity = new CoregCourseEvent();
        eventEntity.setEventStatus(EventStatus.DB_COMMITTED.name());
        when(coregCourseEventRepository.findAllByEventStatusOrderByCreateDate(EventStatus.DB_COMMITTED.name()))
                .thenReturn(List.of(eventEntity));
        // Mock any method you call on applicationProperties
        when(applicationProperties.getPublishCoregEventsThreshold()).thenReturn(100);

        // Act
        scheduler.findAndPublishCoregEventsToJetStream();

        // Assert
        verify(publisher).dispatchChoreographyEvent(any(CoregCourseEvent.class));
    }

    @Test
    void shouldNotPublishIfNoEventsFound() {
        LockAssert.assertLocked();
        // Arrange: repository returns empty list
        when(coregCourseEventRepository.findAllByEventStatusOrderByCreateDate(DB_COMMITTED.name()))
                .thenReturn(List.of());

        // Act
        scheduler.findAndPublishCoregEventsToJetStream();

        // Assert: publisher is never called
        verifyNoInteractions(publisher);
    }
}



