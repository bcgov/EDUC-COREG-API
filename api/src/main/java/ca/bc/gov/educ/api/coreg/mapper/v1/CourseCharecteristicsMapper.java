package ca.bc.gov.educ.api.coreg.mapper.v1;

import ca.bc.gov.educ.api.coreg.mapper.LocalDateTimeMapper;
import ca.bc.gov.educ.api.coreg.mapper.StringMapper;
import ca.bc.gov.educ.api.coreg.model.v1.CourseCharacteristicsEntity;
import ca.bc.gov.educ.api.coreg.struct.v1.CourseCharacteristics;
import ca.bc.gov.educ.api.coreg.mapper.UUIDMapper;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class, StringMapper.class})
public interface CourseCharecteristicsMapper {

    CourseCharecteristicsMapper mapper = Mappers.getMapper(CourseCharecteristicsMapper.class);

    CourseCharacteristics toStructure(CourseCharacteristicsEntity courseCharacteristicsEntity);
}
