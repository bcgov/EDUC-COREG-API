package ca.bc.gov.educ.api.coreg.mapper.v1;

        import ca.bc.gov.educ.api.coreg.mapper.LocalDateTimeMapper;
        import ca.bc.gov.educ.api.coreg.mapper.StringMapper;
        import ca.bc.gov.educ.api.coreg.model.v1.CourseCodeEntity;
        import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
        import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramCoursesEntity;
        import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramRequirementEntity;
        import ca.bc.gov.educ.api.coreg.struct.v1.CourseCode;
        import ca.bc.gov.educ.api.coreg.struct.v1.Courses;
        import ca.bc.gov.educ.api.coreg.struct.v1.GraduationProgramCourses;
        import ca.bc.gov.educ.api.coreg.struct.v1.GraduationProgramRequirement;
        import ca.bc.gov.educ.api.institute.mapper.UUIDMapper;
        import org.mapstruct.Mapper;
        import org.mapstruct.Mapping;
        import org.mapstruct.factory.Mappers;

@Mapper(uses = {UUIDMapper.class, LocalDateTimeMapper.class, StringMapper.class})
public interface GraduationProgramRequirementsMapper {

    GraduationProgramRequirementsMapper mapper = Mappers.getMapper(GraduationProgramRequirementsMapper.class);

    @Mapping(target = "graduationProgramEntity.gradProgramID", source = "gradProgramID")
    GraduationProgramRequirementEntity toModel(GraduationProgramRequirement structure);

    @Mapping(target = "gradProgramID", source = "graduationProgramEntity.gradProgramID")
    GraduationProgramRequirement toStructure(GraduationProgramRequirementEntity coursesEntity);
}

