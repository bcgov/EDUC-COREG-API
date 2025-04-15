package ca.bc.gov.educ.api.coreg.filter;

import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.chrono.ChronoLocalDate;
import java.time.chrono.ChronoLocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class CourseInformationFilterSpecs extends BaseFilterSpecs<CoursesEntity> {

    protected CourseInformationFilterSpecs(FilterSpecifications<CoursesEntity, ChronoLocalDate> dateFilterSpecifications, FilterSpecifications<CoursesEntity, ChronoLocalDateTime<?>> dateTimeFilterSpecifications, FilterSpecifications<CoursesEntity, Integer> integerFilterSpecifications, FilterSpecifications<CoursesEntity, String> stringFilterSpecifications, FilterSpecifications<CoursesEntity, Long> longFilterSpecifications, FilterSpecifications<CoursesEntity, UUID> uuidFilterSpecifications, Converters converters) {
        super(dateFilterSpecifications, dateTimeFilterSpecifications, integerFilterSpecifications, stringFilterSpecifications, longFilterSpecifications, uuidFilterSpecifications, converters);
    }
}
