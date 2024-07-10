package ca.bc.gov.educ.api.coreg.mapper.v1;

import ca.bc.gov.educ.api.coreg.mapper.LocalDateTimeMapper;
import ca.bc.gov.educ.api.coreg.mapper.StringMapper;
import ca.bc.gov.educ.api.coreg.model.v1.CourseCodeEntity;
import ca.bc.gov.educ.api.coreg.struct.v1.CourseCode;
import ca.bc.gov.educ.api.coreg.mapper.UUIDMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class, StringMapper.class})
public interface CourseCodeMapper {

    CourseCodeMapper mapper = Mappers.getMapper(CourseCodeMapper.class);


    @Mapping(target = "coursesEntity.courseID", source = "courseID")
    CourseCodeEntity toModel(CourseCode structure);

    @Mapping(target = "courseID", source = "coursesEntity.courseID")
    CourseCode toStructure(CourseCodeEntity coursesEntity);
}
