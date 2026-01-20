package ca.bc.gov.educ.api.coreg.constants.v1;

import lombok.Getter;

@Getter
public enum CourseSearchReportHeader {
    CODE("Code"),
    LEVEL("Level"),
    STATUS("Status"),
    NAME("Name"),
    OPEN_DATE("Open Date"),
    CLOSE_DATE("Close Date"),
    COMPLETION_DATE("Completion Date"),
    CREDITS("Credits"),
    GENERIC("Generic"),
    INSTRUCTION_LANGUAGE("Instruction Language");

    private final String code;

    CourseSearchReportHeader(String code) {
        this.code = code;
    }
}
