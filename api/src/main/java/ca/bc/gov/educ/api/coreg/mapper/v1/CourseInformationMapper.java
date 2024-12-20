package ca.bc.gov.educ.api.coreg.mapper.v1;

import ca.bc.gov.educ.api.coreg.mapper.LocalDateTimeMapper;
import ca.bc.gov.educ.api.coreg.mapper.StringMapper;
import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import ca.bc.gov.educ.api.coreg.struct.v1.Courses;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {LocalDateTimeMapper.class, StringMapper.class, CourseCodeMapper.class, CourseCharecteristicsMapper.class, CourseAllowableCreditsMapper.class
})
@SuppressWarnings("squid:S1214")
public interface CourseInformationMapper {

  CourseInformationMapper mapper = Mappers.getMapper(CourseInformationMapper.class);


  CoursesEntity toModel(Courses structure);

    Courses toStructure(CoursesEntity coursesEntity);
}
