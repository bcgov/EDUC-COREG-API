package ca.bc.gov.educ.api.coreg.struct.v1;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.io.Serializable;
import java.math.BigInteger;


@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CourseAllowableCredits implements Serializable {

    private BigInteger cacID;

    private Integer creditValue;

    private BigInteger courseID;

    private String startDate;

    private String endDate;


}
