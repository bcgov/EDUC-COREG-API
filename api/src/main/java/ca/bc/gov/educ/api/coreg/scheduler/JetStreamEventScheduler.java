package ca.bc.gov.educ.api.coreg.scheduler;

import ca.bc.gov.educ.api.coreg.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.coreg.model.v1.CoregCourseEvent;
import ca.bc.gov.educ.api.coreg.properties.ApplicationProperties;
import ca.bc.gov.educ.api.coreg.repository.v1.CoregCourseEventRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static ca.bc.gov.educ.api.coreg.constants.v1.EventStatus.*;

@Component
@Slf4j
public class JetStreamEventScheduler {

    private final CoregCourseEventRepository coregCourseEventRepository;
    private final Publisher publisher;
    private final ApplicationProperties applicationProperties;

    public JetStreamEventScheduler(final CoregCourseEventRepository coregCourseEventRepository,
                                   Publisher publisher,
                                   ApplicationProperties applicationProperties) {
        this.coregCourseEventRepository = coregCourseEventRepository;
        this.publisher = publisher;
        this.applicationProperties = applicationProperties;
    }

    /** Wrapper for lock check – override in tests */
    protected void assertTaskLocked() {
        LockAssert.assertLocked(); // production behavior
    }

    @Scheduled(fixedRateString = "${scheduler.publish-coreg-events.rate}")
    @SchedulerLock(name = "PUBLISH_COREG_EVENTS",
            lockAtLeastFor = "${scheduler.publish-coreg-events.lockAtLeastFor}",
            lockAtMostFor = "${scheduler.publish-coreg-events.lockAtMostFor}")
    public void findAndPublishCoregEventsToJetStream() {
        assertTaskLocked(); // call wrapper
        log.debug("Running scheduled task [PUBLISH_COREG_EVENTS] " + java.time.LocalDateTime.now());

        final var results = coregCourseEventRepository.findAllByEventStatusOrderByCreateDate(DB_COMMITTED.name());
        if (!results.isEmpty()) {
            log.debug("{} events found to publish", results.size());
            int count = 0;
            for (CoregCourseEvent el : results.stream().toList()) {
                if (count++ < applicationProperties.getPublishCoregEventsThreshold()) {
                    try {
                        publisher.dispatchChoreographyEvent(el);
                        el.setEventStatus(MESSAGE_PUBLISHED.name());
                        el.setUpdateUser("COREG-SCHEDULER");
                        el.setUpdateDate(LocalDateTime.now());
                        coregCourseEventRepository.save(el);
                    } catch (final Exception ex) {
                        log.error("Exception while trying to publish COREG Course Event", ex);
                    }
                }
            }
            log.debug("PUBLISH_TRAX_UPDATED_EVENTS_TO_JET_STREAM: processing is completed");
        }
    }
}
