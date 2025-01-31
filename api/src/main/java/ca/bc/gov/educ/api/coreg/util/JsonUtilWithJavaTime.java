package ca.bc.gov.educ.api.coreg.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;

/**
 * A separate JSON utility with Java Time handling.
 */
public class JsonUtilWithJavaTime {
    public static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private JsonUtilWithJavaTime() {
    }

    public static String getJsonStringFromObject(Object payload) throws JsonProcessingException {
        return mapper.writeValueAsString(payload);
    }

    public static <T> T getJsonObjectFromString(Class<T> clazz, String payload) throws JsonProcessingException {
        return mapper.readValue(payload, clazz);
    }

    public static byte[] getJsonBytesFromObject(Object payload) throws JsonProcessingException {
        return mapper.writeValueAsBytes(payload);
    }

    public static <T> T getObjectFromJsonBytes(Class<T> clazz, byte[] payload) throws IOException {
        return mapper.readValue(payload, clazz);
    }
}
