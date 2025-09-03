package ca.bc.gov.educ.api.coreg.scheduler;

import ca.bc.gov.educ.api.coreg.messaging.jetstream.Publisher;
import ca.bc.gov.educ.api.coreg.service.v1.CourseRegistryEventService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CoregCourseEventsScheduler {
    private final CourseRegistryEventService courseRegistryEventService;
    private final Publisher publisher;

    @Autowired
    public CoregCourseEventsScheduler(CourseRegistryEventService courseRegistryEventService, Publisher publisher) {
        this.courseRegistryEventService = courseRegistryEventService;
        this.publisher = publisher;
    }

    @Scheduled(fixedRateString = "${scheduler.read-coreg-events.rate}") // Runs every 3 minutes
    @SchedulerLock(name = "READ_COREG_EVENTS", lockAtLeastFor = "${scheduler.read-coreg-events.lockAtLeastFor}", lockAtMostFor = "${scheduler.read-coreg-events.lockAtMostFor}")
    public void readCoregEvents() {
        log.debug("Running scheduled task [READ_COREG_EVENTS] " + java.time.LocalDateTime.now());
        try {
            var events = courseRegistryEventService.readCourseRegistryEvents();
            events.forEach(publisher::dispatchChoreographyEvent);
        } catch (Exception e) {
            log.error("Error while reading course registry events", e);
        }
    }
}
