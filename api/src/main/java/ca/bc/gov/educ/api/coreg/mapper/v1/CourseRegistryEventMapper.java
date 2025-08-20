package ca.bc.gov.educ.api.coreg.mapper.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseDataSourceDTO;
import ca.bc.gov.educ.api.coreg.model.v1.CourseDataSourceEntity;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseRegistryEventMapper {

    CourseRegistryEventMapper INSTANCE = Mappers.getMapper(CourseRegistryEventMapper.class);

    // Event entity -> DTO
    @Mapping(target = "dataSources", source = "dataSourceEntities")
    CourseRegistryEventDTO toDTO(CourseRegistryEventEntity entity);

    List<CourseRegistryEventDTO> toDTOs(List<CourseRegistryEventEntity> entities);

    // DTO -> Entity
    CourseRegistryEventEntity toEntity(CourseRegistryEventDTO dto);

    // Data source entity -> DTO
    CourseDataSourceDTO toDTO(CourseDataSourceEntity entity);

    List<CourseDataSourceDTO> toDataSourceDTOs(List<CourseDataSourceEntity> entities);

    // DTO -> Entity
    CourseDataSourceEntity toEntity(CourseDataSourceDTO dto);
}


