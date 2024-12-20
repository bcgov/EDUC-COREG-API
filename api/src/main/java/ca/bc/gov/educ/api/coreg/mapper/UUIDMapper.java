package ca.bc.gov.educ.api.coreg.mapper;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class UUIDMapper {

    public UUID map(String value) {
        if (StringUtils.isBlank(value)) {
            return null;
        }
        return UUID.fromString(value);
    }

    public String map(UUID value) {
        if (value == null) {
            return null;
        }
        return value.toString();
    }
}
