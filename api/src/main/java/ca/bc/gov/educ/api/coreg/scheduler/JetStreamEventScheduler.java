package ca.bc.gov.educ.api.coreg.scheduler;

import ca.bc.gov.educ.api.coreg.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.coreg.model.v1.CoregCourseEvent;
import ca.bc.gov.educ.api.coreg.repository.v1.CoregCourseEventRepository;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseRegistryEventRepository;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.core.LockAssert;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static ca.bc.gov.educ.api.coreg.constants.v1.EventStatus.DB_COMMITTED;
import static ca.bc.gov.educ.api.coreg.constants.v1.EventStatus.PROCESSED;

@Component
@Slf4j
public class JetStreamEventScheduler {

    /**
     * The EventEntity repository.
     */
    private final CoregCourseEventRepository coregCourseEventRepository;
    private final Publisher publisher;

    /**
     * Instantiates a new Stan event scheduler.
     *
     * @param coregCourseEventRepository the event repository
     * @param publisher   the publisher
     */
    public JetStreamEventScheduler(final CoregCourseEventRepository coregCourseEventRepository, Publisher publisher) {
        this.coregCourseEventRepository = coregCourseEventRepository;
        this.publisher = publisher;
    }

    @Scheduled(fixedRate = 300000) //Every 5 mins
    @SchedulerLock(name = "PUBLISH_COREG_EVENTS_TO_JET_STREAM", lockAtLeastFor = "2m", lockAtMostFor = "4m")
    public void findAndPublishGradStatusEventsToJetStream() {
        LockAssert.assertLocked();
        log.debug("Running scheduled task [PUBLISH_COREG_EVENTS_TO_JET_STREAM] " + java.time.LocalDateTime.now());
        final var results = coregCourseEventRepository.findAllByEventStatusOrderByCreateDate(DB_COMMITTED.name());
        if (!results.isEmpty()) {
            var filteredList = results.stream().filter(el -> el.getUpdateDate().isBefore(LocalDateTime.now().minusMinutes(5))).toList();
            for (CoregCourseEvent el : filteredList) {
                try {
                    publisher.dispatchChoreographyEvent(el);
                    el.setEventStatus(PROCESSED.name());
                    coregCourseEventRepository.save(el);
                } catch (final Exception ex) {
                    log.error("Exception while trying to handle TRAX updated message", ex);
                }
            }
            log.debug("PUBLISH_TRAX_UPDATED_EVENTS_TO_JET_STREAM: processing is completed");
        }
    }

}