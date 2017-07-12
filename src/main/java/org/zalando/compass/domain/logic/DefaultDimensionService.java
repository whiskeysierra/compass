package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.Pagination;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;

import static org.springframework.transaction.annotation.Isolation.SERIALIZABLE;

@Service
class DefaultDimensionService implements DimensionService {

    private final ReplaceDimension replace;
    private final ReadDimension read;
    private final ReadDimensionRevision readRevision;
    private final DeleteDimension delete;

    @Autowired
    DefaultDimensionService(final ReplaceDimension replace, final ReadDimension read,
            final ReadDimensionRevision readRevision, final DeleteDimension delete) {
        this.replace = replace;
        this.read = read;
        this.readRevision = readRevision;
        this.delete = delete;
    }

    @Transactional(isolation = SERIALIZABLE)
    @Override
    public boolean replace(final Dimension dimension, @Nullable final String comment) {
        return replace.replace(dimension, comment);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Dimension> readPage(@Nullable final String term, final Pagination<String> query) {
        return read.readPage(term, query);
    }

    @Transactional(readOnly = true)
    @Override
    public Dimension read(final String id) {
        return read.read(id);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readPageRevisions(final Pagination<Long> query) {
        return readRevision.readPageRevisions(query);
    }

    @Transactional(readOnly = true)
    @Override
    public PageRevision<Dimension> readPageAt(final long revision, final Pagination<String> query) {
        return readRevision.readPageAt(revision, query);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readRevisions(final String id, final Pagination<Long> query) {
        return readRevision.readRevisions(id, query);
    }

    @Transactional(readOnly = true)
    @Override
    public DimensionRevision readAt(final String id, final long revision) {
        return readRevision.readAt(id, revision);
    }

    @Transactional
    @Override
    public void delete(final String id, @Nullable final String comment) {
        delete.delete(id, comment);
    }

}
