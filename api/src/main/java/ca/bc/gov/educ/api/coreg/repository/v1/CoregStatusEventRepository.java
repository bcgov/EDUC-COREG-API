package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CoregStatusEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * The interface Coreg event repository.
 */
public interface CoregStatusEventRepository extends JpaRepository<CoregStatusEvent, UUID> {
    /**
     * Find by saga id optional.
     *
     * @param sagaId the saga id
     * @return the optional
     */
    Optional<CoregStatusEvent> findBySagaId(UUID sagaId);

    /**
     * Find by saga id and event type optional.
     *
     * @param sagaId    the saga id
     * @param eventType the event type
     * @return the optional
     */
    Optional<CoregStatusEvent> findBySagaIdAndEventType(UUID sagaId, String eventType);

    /**
     * Find by event status list.
     *
     * @param eventStatus the event status
     * @return the list
     */
    List<CoregStatusEvent> findByEventStatus(String eventStatus);

    @Transactional
    @Modifying
    @Query("delete from CoregStatusEvent where createDate <= :createDate")
    void deleteByCreateDateBefore(LocalDateTime createDate);
}
