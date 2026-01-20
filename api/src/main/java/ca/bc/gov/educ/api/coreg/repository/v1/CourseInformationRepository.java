package ca.bc.gov.educ.api.coreg.repository.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseDownloadProjection;
import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.stream.Stream;

public interface CourseInformationRepository extends JpaRepository<CoursesEntity, BigInteger>, JpaSpecificationExecutor<CoursesEntity> {

    /**
     * Optimized native query for grad admin CSV download.
     * Fetches only required fields with single query and aggregated credits.
     * Uses streaming to avoid loading all data into memory.
     *
     * @return Stream of CourseDownloadProjection
     */
    @Query(value = "SELECT " +
            "cc.EXTERNAL_CODE as externalCode, " +
            "c.COURSE_TITLE as courseTitle, " +
            "TO_CHAR(c.START_DATE, 'YYYY-MM-DD') as startDate, " +
            "TO_CHAR(c.END_DATE, 'YYYY-MM-DD') as endDate, " +
            "TO_CHAR(c.COMPLETION_END_DATE, 'YYYY-MM-DD') as completionEndDate, " +
            "c.GENERIC_CRSE_TYPE as genericCourseType, " +
            "chr.DESCRIPTION as instructionLanguage, " +
            "LISTAGG(cac.CREDIT_VALUE, ', ') WITHIN GROUP (ORDER BY cac.CREDIT_VALUE) as credits " +
            "FROM COREG.CRSE_COURSES c " +
            "LEFT JOIN COREG.CRSE_COURSE_CODE_MAPPINGS cc " +
            "ON c.CRS_ID = cc.CRS_ID " +
            "AND cc.ORIGINATING_SYSTEM_CHAR_ID = 39 " +
            "LEFT JOIN COREG.CRSE_CHARACTERISTICS chr " +
            "ON c.LANGUAGE_TYPE_CHAR_ID = chr.CHAR_ID " +
            "LEFT JOIN COREG.CRSE_COURSE_ALLOWABLE_CREDITS cac " +
            "ON c.CRS_ID = cac.CRS_ID " +
            "GROUP BY " +
            "cc.EXTERNAL_CODE, " +
            "c.COURSE_TITLE, " +
            "c.START_DATE, " +
            "c.END_DATE, " +
            "c.COMPLETION_END_DATE, " +
            "c.GENERIC_CRSE_TYPE, " +
            "chr.DESCRIPTION " +
            "ORDER BY cc.EXTERNAL_CODE",
            nativeQuery = true)
    Stream<CourseDownloadProjection> streamAllForDownload();
}
