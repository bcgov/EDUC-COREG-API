package ca.bc.gov.educ.api.coreg.filter;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.function.Function;

/**
 * The type Filter specifications.
 *
 * @param <E> the type parameter
 * @param <T> the type parameter
 */
@Service
public class FilterSpecifications<E, T extends Comparable<T>> {

  private EnumMap<FilterOperation, Function<FilterCriteria<T>, Specification<E>>> map;

  /**
   * Instantiates a new Filter specifications.
   */
  public FilterSpecifications() {
    initSpecifications();
  }

  /**
   * Gets specification.
   *
   * @param operation the operation
   * @return the specification
   */
  public Function<FilterCriteria<T>, Specification<E>> getSpecification(FilterOperation operation) {
    return map.get(operation);
  }

  /**
   * Init specifications.
   */
  @PostConstruct
  public void initSpecifications() {

    map = new EnumMap<>(FilterOperation.class);

    // Equal
    map.put(FilterOperation.EQUAL, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> {
      if(filterCriteria.getConvertedSingleValue() == null){
        return criteriaBuilder.isNull(root.get(filterCriteria.getFieldName()));
      }
      return criteriaBuilder
              .equal(getSpecificationPath(root, filterCriteria), filterCriteria.getConvertedSingleValue());
    });

    map.put(FilterOperation.NOT_EQUAL, filterCriteria -> (root, criteriaQuery, criteriaBuilder) -> {
      if(filterCriteria.getConvertedSingleValue() == null){
        return criteriaBuilder.isNotNull(root.get(filterCriteria.getFieldName()));
      }
      return criteriaBuilder
              .notEqual(getSpecificationPath(root, filterCriteria), filterCriteria.getConvertedSingleValue());
    });

    map.put(FilterOperation.GREATER_THAN, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.greaterThan(getSpecificationPath(root, filterCriteria), filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.GREATER_THAN_OR_EQUAL_TO, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.greaterThanOrEqualTo(getSpecificationPath(root, filterCriteria), filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.LESS_THAN, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.lessThan(getSpecificationPath(root, filterCriteria), filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.LESS_THAN_OR_EQUAL_TO, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.lessThanOrEqualTo(getSpecificationPath(root, filterCriteria), filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.IN, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            getSpecificationPath(root, filterCriteria).in(filterCriteria.getConvertedValues()));

    map.put(FilterOperation.NOT_IN, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.not(getSpecificationPath(root, filterCriteria).in(filterCriteria.getConvertedValues())));

    map.put(FilterOperation.BETWEEN, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.between(getSpecificationPath(root, filterCriteria), filterCriteria.getMinValue(), filterCriteria.getMaxValue()));

    map.put(FilterOperation.CONTAINS, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.like(getSpecificationPath(root, filterCriteria), "%" + filterCriteria.getConvertedSingleValue() + "%"));

    map.put(FilterOperation.STARTS_WITH, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.like(getSpecificationPath(root, filterCriteria), filterCriteria.getConvertedSingleValue() + "%"));

    map.put(FilterOperation.NOT_STARTS_WITH, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.notLike(getSpecificationPath(root, filterCriteria), filterCriteria.getConvertedSingleValue() + "%"));

    map.put(FilterOperation.ENDS_WITH, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.like(getSpecificationPath(root, filterCriteria), "%" + filterCriteria.getConvertedSingleValue()));

    map.put(FilterOperation.CONTAINS_IGNORE_CASE, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.like(criteriaBuilder.lower(getSpecificationPath(root, filterCriteria)), "%" + filterCriteria.getConvertedSingleValue() + "%"));

    map.put(FilterOperation.STARTS_WITH_IGNORE_CASE, filterCriteria -> (root, criteriaQuery, criteriaBuilder) ->
            criteriaBuilder.like(criteriaBuilder.lower(getSpecificationPath(root, filterCriteria)), filterCriteria.getConvertedSingleValue() + "%"));
  }

  /**
   * Below method only supports 2 level deep nested properties.
   * @param root
   * @param filterCriteria
   * @return
   */
  private Path getSpecificationPath(Root root, FilterCriteria<T> filterCriteria) {
    if (filterCriteria.getFieldName().contains(".")) {
      String[] splits = filterCriteria.getFieldName().split("\\.");
      if (splits.length ==3) {
        return root.join(splits[0]).get(splits[1]).get(splits[2]);
      }
      if (splits.length ==2) {
        return root.join(splits[0]).get(splits[1]);
      }
    }
    return root.get(filterCriteria.getFieldName());
  }
}

