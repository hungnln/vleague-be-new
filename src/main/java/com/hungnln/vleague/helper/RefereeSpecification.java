package com.hungnln.vleague.helper;

import com.hungnln.vleague.entity.Referee;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

@AllArgsConstructor
public class RefereeSpecification implements Specification<Referee> {
    private SearchCriteria criteria;

    @Override
    public Specification<Referee> and(Specification<Referee> other) {
        return Specification.super.and(other);
    }

    @Override
    public Specification<Referee> or(Specification<Referee> other) {
        return Specification.super.or(other);
    }

    @Override
    public Predicate toPredicate(Root<Referee> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        switch (criteria.getOperation()) {
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
