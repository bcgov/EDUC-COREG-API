package ca.bc.gov.educ.api.coreg.service.v1;

import ca.bc.gov.educ.api.coreg.exception.InvalidParameterException;
import ca.bc.gov.educ.api.coreg.filter.FilterOperation;
import ca.bc.gov.educ.api.coreg.filter.GraduationProgramFilterSpecs;
import ca.bc.gov.educ.api.coreg.model.v1.GraduationProgramEntity;
import ca.bc.gov.educ.api.coreg.repository.v1.GraduationProgramRepository;
import ca.bc.gov.educ.api.coreg.struct.v1.Condition;
import ca.bc.gov.educ.api.coreg.struct.v1.Search;
import ca.bc.gov.educ.api.coreg.struct.v1.SearchCriteria;
import ca.bc.gov.educ.api.coreg.struct.v1.ValueType;
import ca.bc.gov.educ.api.coreg.util.RequestUtil;
import ca.bc.gov.educ.api.coreg.util.TransformUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jboss.threads.EnhancedQueueExecutor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class GraduationProgramService {

    private final GraduationProgramFilterSpecs graduationProgramFilterSpecs;

    private final GraduationProgramRepository graduationProgramRepository;


    public GraduationProgramService(GraduationProgramFilterSpecs graduationProgramFilterSpecs, GraduationProgramRepository graduationProgramRepository) {
        this.graduationProgramFilterSpecs = graduationProgramFilterSpecs;
        this.graduationProgramRepository = graduationProgramRepository;
    }

    private final Executor paginatedQueryExecutor = new EnhancedQueueExecutor.Builder()
            .setThreadFactory(new ThreadFactoryBuilder().setNameFormat("async-pagination-query-executor-%d").build())
            .setCorePoolSize(2).setMaximumPoolSize(10).setKeepAliveTime(Duration.ofSeconds(60)).build();


    public Specification<GraduationProgramEntity> getSpecifications(Specification<GraduationProgramEntity> graduationProgramSpecs, int i, Search search) {
        if (i == 0) {
            graduationProgramSpecs = getSchoolEntitySpecification(search.getSearchCriteriaList());
        } else {
            if (search.getCondition() == Condition.AND) {
                graduationProgramSpecs = graduationProgramSpecs.and(getSchoolEntitySpecification(search.getSearchCriteriaList()));
            } else {
                graduationProgramSpecs = graduationProgramSpecs.or(getSchoolEntitySpecification(search.getSearchCriteriaList()));
            }
        }
        return graduationProgramSpecs;
    }

    private Specification<GraduationProgramEntity> getSpecificationPerGroup(Specification<GraduationProgramEntity> graduationProgramEntitySpecification, int i, SearchCriteria criteria, Specification<GraduationProgramEntity> typeSpecification) {
        if (i == 0) {
             graduationProgramEntitySpecification = Specification.where(typeSpecification);
        } else {
            if (criteria.getCondition() == Condition.AND) {
                graduationProgramEntitySpecification = graduationProgramEntitySpecification.and(typeSpecification);
            } else {
                graduationProgramEntitySpecification = graduationProgramEntitySpecification.or(typeSpecification);
            }
        }
        return graduationProgramEntitySpecification;
    }

    private Specification<GraduationProgramEntity> getSchoolEntitySpecification(List<SearchCriteria> criteriaList) {
        Specification<GraduationProgramEntity> schoolSpecs = null;
        if (!criteriaList.isEmpty()) {
            int i = 0;
            for (SearchCriteria criteria : criteriaList) {
                if (criteria.getKey() != null && criteria.getOperation() != null && criteria.getValueType() != null) {
                    var criteriaValue = criteria.getValue();
                    if(StringUtils.isNotBlank(criteria.getValue()) && TransformUtil.isUppercaseField(GraduationProgramEntity.class, criteria.getKey())) {
                        criteriaValue = criteriaValue.toUpperCase();
                    }
                    Specification<GraduationProgramEntity> typeSpecification = getTypeSpecification(criteria.getKey(), criteria.getOperation(), criteriaValue, criteria.getValueType());
                    schoolSpecs = getSpecificationPerGroup(schoolSpecs, i, criteria, typeSpecification);
                    i++;
                } else {
                    throw new InvalidParameterException("Search Criteria can not contain null values for key, value and operation type");
                }
            }
        }
        return schoolSpecs;
    }

    private Specification<GraduationProgramEntity> getTypeSpecification(String key, FilterOperation filterOperation, String value, ValueType valueType) {
        Specification<GraduationProgramEntity> schoolEntitySpecification = null;
        switch (valueType) {
            case STRING:
                schoolEntitySpecification = graduationProgramFilterSpecs.getStringTypeSpecification(key, value, filterOperation);
                break;
            case DATE_TIME:
                schoolEntitySpecification = graduationProgramFilterSpecs.getDateTimeTypeSpecification(key, value, filterOperation);
                break;
            case LONG:
                schoolEntitySpecification = graduationProgramFilterSpecs.getLongTypeSpecification(key, value, filterOperation);
                break;
            case INTEGER:
                schoolEntitySpecification = graduationProgramFilterSpecs.getIntegerTypeSpecification(key, value, filterOperation);
                break;
            case DATE:
                schoolEntitySpecification = graduationProgramFilterSpecs.getDateTypeSpecification(key, value, filterOperation);
                break;
            case UUID:
                schoolEntitySpecification = graduationProgramFilterSpecs.getUUIDTypeSpecification(key, value, filterOperation);
                break;
            default:
                break;
        }
        return schoolEntitySpecification;
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public CompletableFuture<Page<GraduationProgramEntity>> findAll(Specification<GraduationProgramEntity> gradProgramSpecs, final Integer pageNumber, final Integer pageSize, final List<Sort.Order> sorts) {
        log.trace("In find all query: {}", gradProgramSpecs);
        return CompletableFuture.supplyAsync(() -> {
            Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sorts));
            try {
                log.trace("Running paginated query: {}", gradProgramSpecs);
                var results = this.graduationProgramRepository.findAll(gradProgramSpecs, paging);
                log.trace("Paginated query returned with results: {}", results);
                return results;
            } catch (final Throwable ex) {
                log.error("Failure querying for paginated schools: {}", ex.getMessage());
                throw new CompletionException(ex);
            }
        }, paginatedQueryExecutor);

    }
    public Specification<GraduationProgramEntity> setSpecificationAndSortCriteria(String sortCriteriaJson, String searchCriteriaListJson, ObjectMapper objectMapper, List<Sort.Order> sorts) {
        Specification<GraduationProgramEntity> graduationProgramSpecs = null;
        try {
            RequestUtil.getSortCriteria(sortCriteriaJson, objectMapper, sorts);
            if (StringUtils.isNotBlank(searchCriteriaListJson)) {
                List<Search> searches = objectMapper.readValue(searchCriteriaListJson, new TypeReference<>() {
                });
                int i = 0;
                for (var search : searches) {
                    graduationProgramSpecs = getSpecifications(graduationProgramSpecs, i, search);
                    i++;
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e.getMessage());
        }
        return graduationProgramSpecs;
    }
}

