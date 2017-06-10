package org.zalando.compass.domain.persistence;

import java.util.List;
import java.util.Optional;

public interface Repository<T, K, C> {

    void create(T entity);

    Optional<T> find(K id);

    Optional<T> lock(K id);

    List<T> findAll();

    List<T> findAll(C criteria);

    List<T> lockAll(C criteria);

    void update(T entity);

    boolean delete(K id);

}
