package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.exception.InstituteAPIRuntimeException;
import ca.bc.gov.educ.api.coreg.filter.CourseInformationFilterSpecs;
import ca.bc.gov.educ.api.coreg.model.v1.CoursesEntity;
import ca.bc.gov.educ.api.coreg.repository.v1.CourseInformationRepository;
import ca.bc.gov.educ.api.coreg.struct.v1.Search;
import ca.bc.gov.educ.api.coreg.util.RequestUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseInformationSearchService extends BaseSearchService {

    @Getter
    final CourseInformationFilterSpecs courseFilterSpecs;
    final CourseInformationRepository courseInformationRepository;


    @Transactional(propagation = Propagation.SUPPORTS)
    public CompletableFuture<Page<CoursesEntity>> findAll(Specification<CoursesEntity> specs, final Integer pageNumber, final Integer pageSize, final List<Sort.Order> sorts) {
        log.trace("In find all query: {}", specs);
        return CompletableFuture.supplyAsync(() -> {
            Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sorts));
            try {
                log.trace("Running paginated query specs: {}, paging: {}", specs, paging);
                var results = this.courseInformationRepository.findAll(specs, paging);
                log.trace("Paginated query returned with results: {}", results);
                return results;
            } catch (final Throwable ex) {
                log.error("Failure querying for paginated collections: {}", ex.getMessage());
                throw new CompletionException(ex);
            }
        });
    }


    public Specification<CoursesEntity> setSpecificationAndSortCriteria(String sortCriteriaJson, String searchCriteriaListJson, ObjectMapper objectMapper, List<Sort.Order> sorts) {
        Specification<CoursesEntity> courseSpecs = null;
        try {
            RequestUtil.getSortCriteria(sortCriteriaJson, objectMapper, sorts);
            if (StringUtils.isNotBlank(searchCriteriaListJson)) {
                List<Search> searches = objectMapper.readValue(searchCriteriaListJson, new TypeReference<>() {
                });
                int i = 0;
                for (var search : searches) {
                    courseSpecs = getSpecifications(courseSpecs, i, search, this.getCourseFilterSpecs(), CoursesEntity.class);
                    i++;
                }
            }
        } catch (JsonProcessingException e) {
            throw new InstituteAPIRuntimeException(e.getMessage());
        }
        return courseSpecs;
    }
}
