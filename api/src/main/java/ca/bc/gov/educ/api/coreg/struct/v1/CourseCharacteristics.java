package ca.bc.gov.educ.api.coreg.struct.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCharacteristics implements Serializable {

    private BigInteger id;

    private String type;

    private String code;

    private String description;

}
