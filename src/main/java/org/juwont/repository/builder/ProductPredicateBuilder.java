package org.juwont.repository.builder;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.juwont.entity.Product;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ProductPredicateBuilder {
    private final CriteriaBuilder criteriaBuilder;
    private final Root<Product> root;
    private final List<Predicate> predicates = new ArrayList<>();

    public ProductPredicateBuilder(final CriteriaBuilder criteriaBuilder, final Root<Product> root) {
        this.criteriaBuilder = criteriaBuilder;
        this.root = root;
    }

    public ProductPredicateBuilder filterByStoreName(final String storeName) {
        if (storeName != null) {
            predicates.add(criteriaBuilder.like(root.get("storeName"), "%" + storeName + "%"));
        }
        return this;
    }

    public ProductPredicateBuilder filterByTitle(final String title) {
        if (title != null) {
            predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
        }
        return this;
    }

    public ProductPredicateBuilder filterByGroupTitle(final String groupTitle) {
        if (groupTitle != null) {
            predicates.add(criteriaBuilder.like(root.get("productGroupTitle"), "%" + groupTitle + "%"));
        }
        return this;
    }

    public ProductPredicateBuilder filterByMinReleaseDate(final Instant releaseDate) {
        if (releaseDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("releaseDate"), releaseDate));
        }
        return this;
    }

    public ProductPredicateBuilder filterByMaxReleaseDate(final Instant releaseDate) {
        if (releaseDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("releaseDate"), releaseDate));
        }
        return this;
    }

    public ProductPredicateBuilder filterByMaxGroupReleaseDate(final Instant groupReleaseDate) {
        if (groupReleaseDate != null) {
            predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("productGroupReleaseDate"), groupReleaseDate));
        }
        return this;
    }

    public ProductPredicateBuilder filterByMinGroupReleaseDate(final Instant groupReleaseDate) {
        if (groupReleaseDate != null) {
            predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("productGroupReleaseDate"), groupReleaseDate));
        }
        return this;
    }

    public ProductPredicateBuilder filterByTags(final List<String> tags) {
        if (tags != null) {
            predicates.add(root.join("tags").get("name").in(tags));
        }
        return this;
    }

    public Predicate build() {
        return criteriaBuilder.and(predicates.toArray(Predicate[]::new));
    }
}
