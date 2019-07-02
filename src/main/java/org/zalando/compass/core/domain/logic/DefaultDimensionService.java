package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.core.domain.api.DimensionService;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Revisioned;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DefaultDimensionService implements DimensionService {

    private final ReplaceDimension replace;
    private final ReadDimension read;
    private final DeleteDimension delete;

    @Transactional(isolation = SERIALIZABLE)
    @Override
    public boolean replace(final Dimension dimension, @Nullable final String comment) {
        return replace.replace(dimension, comment);
    }

    @Transactional(isolation = SERIALIZABLE)
    @Override
    public void create(final Dimension dimension, @Nullable final String comment) {
        replace.create(dimension, comment);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Dimension> readPage(@Nullable final String term, final Pagination<String> query) {
        return read.readPage(term, query);
    }

    @Transactional(readOnly = true)
    @Override
    public Revisioned<Dimension> read(final String id) {
        return read.read(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Dimension readOnly(final String id) {
        return read.readOnly(id);
    }

    @Transactional // TODO isolation?!
    @Override
    public void delete(final Dimension dimension, @Nullable final String comment) {
        delete.delete(dimension, comment);
    }

}
