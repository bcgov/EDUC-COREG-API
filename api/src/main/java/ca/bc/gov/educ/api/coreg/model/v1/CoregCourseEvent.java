package ca.bc.gov.educ.api.coreg.model.v1;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "COREG_COURSE_EVENTS")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CoregCourseEvent {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
            name = "UUID",
            strategy = "org.hibernate.id.UUIDGenerator",
            parameters = {
                    @Parameter(name = "uuid_gen_strategy_class",
                            value = "org.hibernate.id.uuid.CustomVersionOneStrategy")
            }
    )
    @Column(name = "COREG_COURSE_EVENT_ID", columnDefinition = "RAW(16)")
    private UUID coregCourseEventId;

    @Lob
    @Column(name = "EVENT_PAYLOAD", nullable = false)
    private byte[] eventPayload;

    @Column(name = "EVENT_STATUS", length = 50, nullable = false)
    private String eventStatus;

    @Column(name = "EVENT_TYPE", length = 100, nullable = false)
    private String eventType;

    @Column(name = "CRSREGEV_ID", nullable = false)
    private Long crsregevId;

    @Column(name = "SAGA_ID", columnDefinition = "RAW(16)")
    private UUID sagaId;

    @Column(name = "EVENT_OUTCOME", length = 100, nullable = false)
    private String eventOutcome;

    @Column(name = "REPLY_CHANNEL", length = 100)
    private String replyChannel;

    @Column(name = "CREATE_USER", length = 32)
    private String createUser;

    @Column(name = "CREATE_DATE", insertable = false, updatable = false)
    private LocalDateTime createDate;

    @Column(name = "UPDATE_USER", length = 32)
    private String updateUser;

    @Column(name = "UPDATE_DATE", insertable = false, updatable = false)
    private LocalDateTime updateDate;
}
