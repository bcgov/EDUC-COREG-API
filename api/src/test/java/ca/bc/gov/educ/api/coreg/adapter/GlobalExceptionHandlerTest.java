package ca.bc.gov.educ.api.coreg.adapter;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import org.springframework.validation.BeanPropertyBindingResult;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void handleMethodArgumentNotValidException_shouldReturnBadRequest() {
        // Arrange
        BeanPropertyBindingResult bindingResult =
                new BeanPropertyBindingResult(new Object(), "testObject");
        bindingResult.addError(new FieldError("testObject", "name", "must not be blank"));

        MethodArgumentNotValidException ex =
                new MethodArgumentNotValidException(null, bindingResult);

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("name", "must not be blank");
    }

    @Test
    void handleConstraintViolationException_shouldReturnBadRequest() {
        // Arrange
        @SuppressWarnings("unchecked")
        ConstraintViolation<Object> violation = mock(ConstraintViolation.class);
        when(violation.getInvalidValue()).thenReturn("badValue");
        when(violation.getMessage()).thenReturn("must not be null");

        Set<ConstraintViolation<?>> violations = Set.of(violation);
        ConstraintViolationException ex = new ConstraintViolationException(violations);

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleValidationExceptions(ex);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(400);
        assertThat(response.getBody()).containsEntry("badValue", "must not be null");
    }

    @Test
    void handleGenericException_shouldReturnInternalServerError() {
        // Arrange
        Exception ex = new Exception("Something went wrong");

        // Act
        ResponseEntity<Map<String, String>> response = handler.handleGenericException(ex);

        // Assert
        assertThat(response.getStatusCodeValue()).isEqualTo(500);
        assertThat(response.getBody()).containsEntry("error", "Something went wrong");
    }
}
