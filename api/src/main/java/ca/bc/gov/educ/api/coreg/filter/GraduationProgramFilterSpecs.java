package ca.bc.gov.educ.api.coreg.filter;

import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramCoursesEntity;
import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class GraduationProgramFilterSpecs extends BaseFilterSpecs<GraduationProgramEntity> {
    /**
     * Instantiates a new Base filter specs.
     *
     * @param dateFilterSpecifications     the date filter specifications
     * @param dateTimeFilterSpecifications the date time filter specifications
     * @param integerFilterSpecifications  the integer filter specifications
     * @param stringFilterSpecifications   the string filter specifications
     * @param longFilterSpecifications     the long filter specifications
     * @param uuidFilterSpecifications     the uuid filter specifications
     * @param converters                   the converters
     */
    protected GraduationProgramFilterSpecs(FilterSpecifications<GraduationProgramEntity, ChronoLocalDate> dateFilterSpecifications, FilterSpecifications<GraduationProgramEntity, ChronoLocalDateTime<?>> dateTimeFilterSpecifications, FilterSpecifications<GraduationProgramEntity, Integer> integerFilterSpecifications, FilterSpecifications<GraduationProgramEntity, String> stringFilterSpecifications, FilterSpecifications<GraduationProgramEntity, Long> longFilterSpecifications, FilterSpecifications<GraduationProgramEntity, UUID> uuidFilterSpecifications, Converters converters) {
        super(dateFilterSpecifications, dateTimeFilterSpecifications, integerFilterSpecifications, stringFilterSpecifications, longFilterSpecifications, uuidFilterSpecifications, converters);
    }
}
