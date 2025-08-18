package ca.bc.gov.educ.api.coreg.mapper.v1;

import ca.bc.gov.educ.api.coreg.model.v1.CourseDataSourceDTO;
import ca.bc.gov.educ.api.coreg.model.v1.CourseDataSourceEntity;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventDTO;
import ca.bc.gov.educ.api.coreg.model.v1.CourseRegistryEventEntity;
import org.mapstruct.*;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CourseRegistryEventMapper {

    CourseDataSourceDTO toCourseDataSourceDTO(CourseDataSourceEntity entity);

    @Mapping(source = "dataSourceEntities", target = "dataSources")
    CourseRegistryEventDTO toCourseRegistryEventDTO(CourseRegistryEventEntity entity);

    List<CourseRegistryEventDTO> toCourseRegistryEventDTOs(List<CourseRegistryEventEntity> entities);
}

