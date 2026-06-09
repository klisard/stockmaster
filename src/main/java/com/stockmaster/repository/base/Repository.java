package com.stockmaster.repository.base;

import java.util.List;
import java.util.Optional;

/**
 * Generic base repository contract (R2: Generics).
 *
 * <p>Defines the standard persistence operations every entity repository must
 * support, parameterised over the entity type {@code <T>}. Spring Data's
 * {@code JpaRepository} provides a richer superset of this contract; this
 * interface documents the minimal generic abstraction taught in Week 3 and
 * makes the type-parameter usage explicit for the project requirement.
 *
 * @param <T> the entity type managed by the repository
 */
public interface Repository<T> {

    T save(T entity);

    Optional<T> findById(Long id);

    List<T> findAll();

    void deleteById(Long id);

    boolean existsById(Long id);
}
