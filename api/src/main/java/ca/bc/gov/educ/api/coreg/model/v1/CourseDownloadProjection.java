package ca.bc.gov.educ.api.coreg.model.v1;

/**
 * Projection interface for course download CSV.
 * Uses native SQL to fetch only required fields with optimal joins.
 */
public interface CourseDownloadProjection {
    String getExternalCode();
    String getCourseTitle();
    String getStartDate();
    String getEndDate();
    String getCompletionEndDate();
    String getGenericCourseType();
    String getInstructionLanguage();
    String getCredits();
}
