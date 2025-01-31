package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.model.v1.ChoreographedEvent;
import ca.bc.gov.educ.api.coreg.repository.v1.CoregStatusEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.UUID;

import static ca.bc.gov.educ.api.coreg.constants.v1.EventStatus.MESSAGE_PUBLISHED;

/**
 * This class will process events from Jet Stream, which is used in choreography pattern, where messages are published if a coreg status is created or updated.
 */
@Service
@Slf4j
public class JetStreamEventHandlerService {

    private final CoregStatusEventRepository coregStatusEventRepository;


    /**
     * Instantiates a new Stan event handler service.
     *
     * @param coregStatusEventRepository the coreg status event repository
     */
    @Autowired
    public JetStreamEventHandlerService(CoregStatusEventRepository coregStatusEventRepository) {
        this.coregStatusEventRepository = coregStatusEventRepository;
    }

    /**
     * Update event status.
     *
     * @param choreographedEvent the choreographed event
     */
    @Transactional
    public void updateEventStatus(ChoreographedEvent choreographedEvent) {
        if (choreographedEvent != null && choreographedEvent.getEventID() != null) {
            var eventID = UUID.fromString(choreographedEvent.getEventID());
            var eventOptional = coregStatusEventRepository.findById(eventID);
            if (eventOptional.isPresent()) {
                var coregEvent = eventOptional.get();
                coregEvent.setEventStatus(MESSAGE_PUBLISHED.toString());
                coregStatusEventRepository.save(coregEvent);
            }
        }
    }
}
