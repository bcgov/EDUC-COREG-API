package ca.bc.gov.educ.api.coreg.mapper.v1;

import ca.bc.gov.educ.api.coreg.mapper.LocalDateTimeMapper;
import ca.bc.gov.educ.api.coreg.mapper.StringMapper;
import ca.bc.gov.educ.api.coreg.mapper.UUIDMapper;
import ca.bc.gov.educ.api.coreg.model.v1.CourseAllowableCreditEntity;
import ca.bc.gov.educ.api.coreg.struct.v1.CourseAllowableCredits;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class, StringMapper.class})
public interface CourseAllowableCreditsMapper {

    CourseAllowableCreditsMapper mapper = Mappers.getMapper(CourseAllowableCreditsMapper.class);


    @Mapping(target = "coursesEntity.courseID", source = "courseID")
    CourseAllowableCreditEntity toModel(CourseAllowableCredits structure);

    @Mapping(target = "courseID", source = "coursesEntity.courseID")
    CourseAllowableCredits toStructure(CourseAllowableCreditEntity coursesEntity);
}
