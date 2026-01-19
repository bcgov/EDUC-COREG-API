package ca.bc.gov.educ.api.coreg.constants.v1;

import lombok.Getter;

/**
 * The enum Event outcome.
 */
@Getter
public enum EventOutcome {
    /**
     * Course found event outcome.
     */
    COURSE_FOUND(1),
    /**
     * Course not found event outcome.
     */
    COURSE_NOT_FOUND(2),
    COURSE_CREATED(41),
    COURSE_UPDATED(42),
    COURSE_DELETED(43);

    private final long code;

    EventOutcome(long code) {
        this.code = code;
    }

    // Optional: get enum by code
    public static EventOutcome fromCode(long code) {
        for (EventOutcome type : EventOutcome.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown code: " + code);
    }
}
