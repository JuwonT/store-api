package org.juwont.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.juwont.repository.builder.ProductPredicateBuilder;
import org.juwont.entity.Product;
import org.juwont.web.dto.ProductFilterDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepository {

    @Autowired
    private final EntityManager entityManager;

    public Product create(final Product product) {
        entityManager.persist(product);
        entityManager.flush();
        return product;
    }

    public Optional<Product> findProduct(final Long id) {
        return Optional.ofNullable(entityManager.find(Product.class, id));
    }

    public Product update(final Product product) {
        entityManager.merge(product);
        return product;
    }

    public List<Product> filter(final ProductFilterDTO filters) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
        final Root<Product> root = query.from(Product.class);

        final ProductPredicateBuilder predicateBuilder = new ProductPredicateBuilder(criteriaBuilder, root);
        final Predicate filterPredicate = predicateBuilder
                .filterByStoreName(filters.storeName())
                .filterByTitle(filters.title())
                .filterByGroupTitle(filters.groupTitle())
                .filterByMinReleaseDate(filters.releaseDateBefore())
                .filterByMaxReleaseDate(filters.releaseDateAfter())
                .filterByMaxGroupReleaseDate(filters.groupReleaseDateAfter())
                .filterByMinGroupReleaseDate(filters.groupReleaseDateBefore())
                .filterByTags(filters.tags())
                .build();

        return entityManager.createQuery(query.where(filterPredicate))
                .setFirstResult((filters.page() - 1) * filters.size())
                .setMaxResults(filters.size())
                .getResultList();
    }

    public void delete(final Product product) {
        entityManager.remove(product);
        entityManager.flush();
    }
}
