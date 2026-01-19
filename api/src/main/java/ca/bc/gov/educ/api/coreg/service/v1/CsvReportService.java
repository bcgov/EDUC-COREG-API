package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.constants.v1.CourseSearchReportHeader;
import ca.bc.gov.educ.api.coreg.exception.CoregAPIRuntimeException;
import ca.bc.gov.educ.api.coreg.model.v1.CourseDownloadProjection;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseInformationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class CsvReportService {

    private final CourseInformationRepository courseInformationRepository;

    /**
     * Generate course search report and stream directly to HTTP response.
     * Uses optimized native SQL query for maximum performance.
     *
     * @param response HTTP response to stream CSV to
     * @throws IOException if writing to response fails
     */
    public void generateCourseReportStream(HttpServletResponse response) throws IOException {
        log.info("Starting course CSV download generation");
        long startTime = System.currentTimeMillis();

        List<String> headers = Arrays.stream(CourseSearchReportHeader.values())
                .map(CourseSearchReportHeader::getCode)
                .toList();

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=\"Courses_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) + ".csv\"");

        CSVFormat csvFormat = CSVFormat.DEFAULT.builder().build();

        try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(response.getOutputStream()), 65536);
             CSVPrinter csvPrinter = new CSVPrinter(writer, csvFormat);
             Stream<CourseDownloadProjection> courseStream = courseInformationRepository.streamAllForDownload()) {

            csvPrinter.printRecord(headers);

            courseStream
                    .map(this::prepareCourseDataForCsv)
                    .forEach(csvRowData -> {
                        try {
                            csvPrinter.printRecord(csvRowData);
                        } catch (IOException e) {
                            throw new CoregAPIRuntimeException(e.getMessage());
                        }
                    });

            csvPrinter.flush();

            long endTime = System.currentTimeMillis();
            log.info("Course CSV download completed in {} ms", (endTime - startTime));
        }
    }

    /**
     * Prepare course data for CSV export from projection.
     *
     * @param course The course projection to convert
     * @return List of strings representing the CSV row
     */
    private List<String> prepareCourseDataForCsv(CourseDownloadProjection course) {
        // Extract Code and Level from external code
        String code = "";
        String level = "";
        if (course.getExternalCode() != null && !course.getExternalCode().isEmpty()) {
            String[] parts = course.getExternalCode().split(" ");
            if (parts.length >= 1) {
                code = parts[0].trim();
            }
            if (parts.length >= 2) {
                level = parts[1].trim();
            }
        }

        String status = (course.getEndDate() == null || course.getEndDate().isEmpty()) ? "Open" : "Closed";
        String name = course.getCourseTitle() != null ? course.getCourseTitle() : "";
        String openDate = course.getStartDate() != null ? course.getStartDate() : "";
        String closeDate = course.getEndDate() != null ? course.getEndDate() : "";
        String completionDate = course.getCompletionEndDate() != null ? course.getCompletionEndDate() : "";
        String credits = course.getCredits() != null ? course.getCredits() : "";
        String generic = "G".equals(course.getGenericCourseType()) ? "Y" : "N";
        String instructionLanguage = course.getInstructionLanguage() != null ? course.getInstructionLanguage() : "";

        return new ArrayList<>(Arrays.asList(
                code,
                level,
                status,
                name,
                openDate,
                closeDate,
                completionDate,
                credits,
                generic,
                instructionLanguage
        ));
    }
}
