package com.hungnln.vleague.helper;

import com.hungnln.vleague.entity.Match;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.sql.Timestamp;
import java.util.Date;

@AllArgsConstructor
public class MatchSpecification implements Specification<Match> {
    private SearchCriteria criteria;

    @Override
    public Specification<Match> and(Specification<Match> other) {
        return Specification.super.and(other);
    }

    @Override
    public Specification<Match> or(Specification<Match> other) {
        return Specification.super.or(other);
    }

    @Override
    public Predicate toPredicate(Root<Match> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        switch (criteria.getOperation()) {
            case GREATER_THAN_OR_EQUAL_DATE:
                return criteriaBuilder.greaterThanOrEqualTo(root.get(criteria.getKey()),(Timestamp) criteria.getValue());
            case LESS_THAN_OR_EQUAL_DATE:
                return criteriaBuilder.lessThanOrEqualTo(root.get(criteria.getKey()), (Date) criteria.getValue());
            case ISMEMBER:
                return criteriaBuilder.isMember(criteria.getValue(), root.get(criteria.getKey()));
            case EQUALITY:
                return criteriaBuilder.equal(root.get(criteria.getKey()), criteria.getValue());
            case NEGATION:
                return criteriaBuilder.notEqual(root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(root.<String> get(
                        criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return criteriaBuilder.lessThan(root.<String> get(
                        criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return criteriaBuilder.like(root.<String> get(
                        criteria.getKey()), criteria.getValue().toString());
            case STARTS_WITH:
                return criteriaBuilder.like(root.<String> get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH:
                return criteriaBuilder.like(root.<String> get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS:
                return criteriaBuilder.like(root.<String> get(
                        criteria.getKey()), "%" + criteria.getValue() + "%");
            default:
                return null;
        }
    }
}
