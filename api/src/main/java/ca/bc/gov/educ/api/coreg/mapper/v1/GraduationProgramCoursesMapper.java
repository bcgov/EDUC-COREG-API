package ca.bc.gov.educ.api.coreg.mapper.v1;

import ca.bc.gov.educ.api.coreg.mapper.LocalDateTimeMapper;
import ca.bc.gov.educ.api.coreg.mapper.StringMapper;
import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramCoursesEntity;
import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramRequirementEntity;
import ca.bc.gov.educ.api.coreg.struct.v1.GraduationProgramCourses;
import ca.bc.gov.educ.api.coreg.struct.v1.GraduationProgramRequirement;
import ca.bc.gov.educ.api.institute.mapper.UUIDMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class, StringMapper.class})
public interface GraduationProgramCoursesMapper {

    GraduationProgramCoursesMapper mapper = Mappers.getMapper(GraduationProgramCoursesMapper.class);

    GraduationProgramCoursesEntity toModel(GraduationProgramCourses structure);

    GraduationProgramCourses toStructure(GraduationProgramCoursesEntity coursesEntity);
}

