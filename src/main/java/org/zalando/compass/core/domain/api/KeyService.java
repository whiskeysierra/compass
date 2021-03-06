package org.zalando.compass.core.domain.api;

import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Revisioned;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;

public interface KeyService {

    boolean replace(Key key, @Nullable String comment);

    void create(Key key, @Nullable String comment) throws EntityAlreadyExistsException;

    PageResult<Key> readPage(@Nullable String term, final Pagination<String> query);

    Revisioned<Key> read(String id);

    Key readOnly(String id);

    void delete(Key key, @Nullable String comment);

}
