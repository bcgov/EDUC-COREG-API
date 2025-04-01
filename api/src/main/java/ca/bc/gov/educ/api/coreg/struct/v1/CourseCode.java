package ca.bc.gov.educ.api.coreg.struct.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigInteger;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseCode  implements Serializable {

    private BigInteger courseID;

    private String externalCode;

    private Integer originatingSystem;

}
