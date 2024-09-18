package ca.bc.gov.educ.api.coreg.mapper.v1;

import ca.bc.gov.educ.api.coreg.mapper.LocalDateTimeMapper;
import ca.bc.gov.educ.api.coreg.mapper.StringMapper;
import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramEntity;
import ca.bc.gov.educ.api.coreg.struct.v1.GraduationProgram;
import ca.bc.gov.educ.api.coreg.struct.v1.GraduationProgramRequirement;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(uses = {LocalDateTimeMapper.class, StringMapper.class, GraduationProgramRequirementsMapper.class})
public interface GraduationProgramMapper {

    GraduationProgramMapper mapper = Mappers.getMapper(GraduationProgramMapper.class);

 GraduationProgram toStructure (GraduationProgramEntity entity);
}
