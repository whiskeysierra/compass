package org.zalando.compass.domain.api;

import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Revisioned;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;

public interface DimensionService {

    boolean replace(Dimension dimension, @Nullable String comment);

    void create(final Dimension dimension, @Nullable String comment) throws EntityAlreadyExistsException;

    PageResult<Dimension> readPage(@Nullable String term, final Pagination<String> query);

    Revisioned<Dimension> read(String id);

    Dimension readOnly(String id);

    void delete(String id, @Nullable String comment);

}
