package ca.bc.gov.educ.api.coreg.scheduler;

import ca.bc.gov.educ.api.coreg.service.v1.CourseRegistryEventService;
import lombok.extern.slf4j.Slf4j;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CoregCourseEventsScheduler {
    private final CourseRegistryEventService courseRegistryEventService;

    @Autowired
    public CoregCourseEventsScheduler(CourseRegistryEventService courseRegistryEventService) {
        this.courseRegistryEventService = courseRegistryEventService;
    }

    @Scheduled(fixedRate = 180000) // Runs every 3 minutes
    @SchedulerLock(name = "READ_COREG_EVENTS", lockAtMostFor = "2m", lockAtLeastFor = "1m")
    public void pullCoregEvents() {
       log.debug("Running scheduled task [READ_COREG_EVENTS] " + java.time.LocalDateTime.now());
       courseRegistryEventService.readCourseRegistryEvents();
    }
}
