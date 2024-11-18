package org.juwont.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.juwont.entity.Tag;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class TagRepository {

    @PersistenceContext
    private final EntityManager entityManager;

    public Tag create(final Tag tag) {
        entityManager.persist(tag);
//        entityManager.flush();
        return tag;
    }

    public Optional<Tag> findByName(String name) {
        final CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tag> query = criteriaBuilder.createQuery(Tag.class);
        final Root<Tag> root = query.from(Tag.class);

        final Predicate predicate = criteriaBuilder.equal(root.get("name"), name);
        return entityManager.createQuery(query.where(predicate))
                .getResultStream()
                .findFirst();
    }
}
