package org.zalando.compass.domain.persistence;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface Repository<T, K, C> {

    boolean create(final T entity) throws IOException;

    default boolean exists(final K id) throws IOException {
        return find(id).isPresent();
    }

    @Nonnull
    default T read(final K id) throws IOException, NotFoundException {
        return find(id).orElseThrow(NotFoundException::new);
    }

    Optional<T> find(final K id) throws IOException;

    List<T> findAll() throws IOException;

    List<T> findAll(final C criteria) throws IOException;

    boolean update(final T entity) throws IOException;

    void delete(final K id) throws IOException;

}
