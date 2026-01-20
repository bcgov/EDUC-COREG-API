package ca.bc.gov.educ.api.coreg.service;

import ca.bc.gov.educ.api.coreg.controller.v1.BaseIntegrationTest;
import ca.bc.gov.educ.api.coreg.model.v1.*;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseInformationRepository;
import ca.bc.gov.educ.api.coreg.service.v1.CsvReportService;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
@ExtendWith(MockitoExtension.class)
public class CsvReportServiceTest extends BaseIntegrationTest {

    @Mock
    private CourseInformationRepository courseInformationRepository;

    @InjectMocks
    private CsvReportService csvReportService;

    @Test
    public void testGenerateCourseReportStream_WithValidData() throws IOException {
        // Arrange
        CourseDownloadProjection projection = createMockProjection(
                "MATH 12",
                "Mathematics 12",
                "2024-09-01",
                "2025-06-30",
                "2025-08-31",
                "N",
                "English",
                "2, 4"
        );

        when(courseInformationRepository.streamAllForDownload())
                .thenReturn(Stream.of(projection));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csvReportService.generateCourseReportStream(response);

        // Assert
        assertThat(response.getContentType()).isEqualTo("text/csv");
        assertThat(response.getHeader("Content-Disposition")).contains("attachment");
        assertThat(response.getHeader("Content-Disposition")).contains("Courses_");
        assertThat(response.getHeader("Content-Disposition")).contains(".csv");

        String csvContent = response.getContentAsString();
        assertThat(csvContent).isNotEmpty();

        assertThat(csvContent).contains("Code");
        assertThat(csvContent).contains("Level");
        assertThat(csvContent).contains("Status");
        assertThat(csvContent).contains("Name");
        assertThat(csvContent).contains("Open Date");
        assertThat(csvContent).contains("Close Date");
        assertThat(csvContent).contains("Completion Date");
        assertThat(csvContent).contains("Credits");
        assertThat(csvContent).contains("Generic");
        assertThat(csvContent).contains("Instruction Language");

        assertThat(csvContent).contains("MATH");
        assertThat(csvContent).contains("12");
        assertThat(csvContent).contains("Closed");
        assertThat(csvContent).contains("Mathematics 12");
        assertThat(csvContent).contains("2024-09-01");
        assertThat(csvContent).contains("2025-06-30");
        assertThat(csvContent).contains("2025-08-31");
        assertThat(csvContent).contains("2, 4");
        assertThat(csvContent).contains("English");

        verify(courseInformationRepository, times(1)).streamAllForDownload();
    }

    @Test
    public void testGenerateCourseReportStream_WithOpenCourse() throws IOException {
        // Arrange
        CourseDownloadProjection projection = createMockProjection(
                "ENGL 10",
                "English 10",
                "2024-09-01",
                null,
                null,
                "N",
                "English",
                "4"
        );

        when(courseInformationRepository.streamAllForDownload())
                .thenReturn(Stream.of(projection));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csvReportService.generateCourseReportStream(response);

        // Assert
        String csvContent = response.getContentAsString();
        assertThat(csvContent).contains("ENGL");
        assertThat(csvContent).contains("10");
        assertThat(csvContent).contains("Open");
        assertThat(csvContent).contains("English 10");
    }

    @Test
    public void testGenerateCourseReportStream_WithGenericCourse() throws IOException {
        // Arrange
        CourseDownloadProjection projection = createMockProjection(
                "GEN 11",
                "Generic Course 11",
                "2024-09-01",
                null,
                null,
                "G",
                "French",
                "4"
        );

        when(courseInformationRepository.streamAllForDownload())
                .thenReturn(Stream.of(projection));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csvReportService.generateCourseReportStream(response);

        // Assert
        String csvContent = response.getContentAsString();
        assertThat(csvContent).contains("GEN");
        assertThat(csvContent).contains("11");
        assertThat(csvContent).contains("Y");
        assertThat(csvContent).contains("French");
    }

    @Test
    public void testGenerateCourseReportStream_WithMultipleCourses() throws IOException {
        // Arrange
        CourseDownloadProjection projection1 = createMockProjection(
                "MATH 10",
                "Mathematics 10",
                "2024-09-01",
                null,
                null,
                "N",
                "English",
                "4"
        );

        CourseDownloadProjection projection2 = createMockProjection(
                "SCI 10",
                "Science 10",
                "2024-09-01",
                "2025-06-30",
                null,
                "N",
                "English",
                "4"
        );

        when(courseInformationRepository.streamAllForDownload())
                .thenReturn(Stream.of(projection1, projection2));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csvReportService.generateCourseReportStream(response);

        // Assert
        String csvContent = response.getContentAsString();

        // Verify both courses are present
        assertThat(csvContent).contains("MATH");
        assertThat(csvContent).contains("Mathematics 10");
        assertThat(csvContent).contains("SCI");
        assertThat(csvContent).contains("Science 10");

        String[] lines = csvContent.split("\n");
        assertThat(lines.length).isGreaterThanOrEqualTo(3); // Header + 2 data rows
    }

    @Test
    public void testGenerateCourseReportStream_WithNullValues() throws IOException {
        // Arrange - Course with null/empty values
        CourseDownloadProjection projection = createMockProjection(
                null,
                "Test Course",
                null,
                null,
                null,
                null,
                null,
                null
        );

        when(courseInformationRepository.streamAllForDownload())
                .thenReturn(Stream.of(projection));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csvReportService.generateCourseReportStream(response);

        // Assert
        String csvContent = response.getContentAsString();
        assertThat(csvContent).isNotEmpty();
        assertThat(csvContent).contains("Test Course");
        assertThat(csvContent).contains(",,");
    }

    @Test
    public void testGenerateCourseReportStream_WithSpecialCharacters() throws IOException {
        // Arrange
        CourseDownloadProjection projection = createMockProjection(
                "TEST 12",
                "Test \"Quote\" Course, with comma",
                "2024-09-01",
                null,
                null,
                "N",
                "English, French",
                "2, 4"
        );

        when(courseInformationRepository.streamAllForDownload())
                .thenReturn(Stream.of(projection));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csvReportService.generateCourseReportStream(response);

        // Assert
        String csvContent = response.getContentAsString();
        assertThat(csvContent).isNotEmpty();
        // CSV should properly escape special characters
        assertThat(csvContent).contains("TEST");
        assertThat(csvContent).contains("12");
    }

    @Test
    public void testGenerateCourseReportStream_WithDifferentCodeFormats() throws IOException {
        // Test different code formats (space vs dash)
        CourseDownloadProjection projection1 = createMockProjection(
                "MATH 12",
                "Math with space",
                "2024-09-01",
                null,
                null,
                "N",
                "English",
                "4"
        );

        CourseDownloadProjection projection2 = createMockProjection(
                "ENGL-10",
                "English with dash",
                "2024-09-01",
                null,
                null,
                "N",
                "English",
                "4"
        );

        when(courseInformationRepository.streamAllForDownload())
                .thenReturn(Stream.of(projection1, projection2));

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csvReportService.generateCourseReportStream(response);

        // Assert
        String csvContent = response.getContentAsString();
        assertThat(csvContent).contains("MATH");
        assertThat(csvContent).contains("12");
        assertThat(csvContent).contains("ENGL-10");
    }

    @Test
    public void testGenerateCourseReportStream_EmptyStream() throws IOException {
        // Arrange
        when(courseInformationRepository.streamAllForDownload())
                .thenReturn(Stream.empty());

        MockHttpServletResponse response = new MockHttpServletResponse();

        // Act
        csvReportService.generateCourseReportStream(response);

        // Assert
        String csvContent = response.getContentAsString();
        assertThat(csvContent).isNotEmpty();

        // Should only have header row
        String[] lines = csvContent.split("\n");
        assertThat(lines.length).isEqualTo(1);

        assertThat(csvContent).contains("Code");
        assertThat(csvContent).contains("Level");
        assertThat(csvContent).contains("Status");
    }

    /**
     * Helper method to create a mock CourseDownloadProjection
     */
    private CourseDownloadProjection createMockProjection(
            String externalCode,
            String courseTitle,
            String startDate,
            String endDate,
            String completionEndDate,
            String genericCourseType,
            String instructionLanguage,
            String credits) {

        return new CourseDownloadProjection() {
            @Override
            public String getExternalCode() { return externalCode; }

            @Override
            public String getCourseTitle() { return courseTitle; }

            @Override
            public String getStartDate() { return startDate; }

            @Override
            public String getEndDate() { return endDate; }

            @Override
            public String getCompletionEndDate() { return completionEndDate; }

            @Override
            public String getGenericCourseType() { return genericCourseType; }

            @Override
            public String getInstructionLanguage() { return instructionLanguage; }

            @Override
            public String getCredits() { return credits; }
        };
    }
}
