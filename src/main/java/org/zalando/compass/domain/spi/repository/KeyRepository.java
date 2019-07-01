package org.zalando.compass.domain.spi.repository;

import org.zalando.compass.domain.model.Key;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public interface KeyRepository {

    void create(Key key);

    List<Key> findAll(@Nullable String term, Pagination<String> query);

    Optional<Key> find(String id);

    void update(Key key);

    void delete(Key key);

}
