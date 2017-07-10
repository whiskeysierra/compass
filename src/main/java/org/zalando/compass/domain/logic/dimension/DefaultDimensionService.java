package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.util.List;

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

    @Transactional
    @Override
    public boolean replace(final Dimension dimension, @Nullable final String comment) {
        return replace.replace(dimension, comment);
    }

    @Override
    public Page<Dimension> readPage(@Nullable final String term) {
        return new Page<>(read.readPage(term), null);
    }

    @Override
    public Dimension read(final String id) {
        return read.read(id);
    }

    @Override
    public Page<Revision> readPageRevisions(final int limit, @Nullable final Long after) {
        return readRevision.readPageRevisions(limit, after);
    }

    @Override
    public PageRevision<Dimension> readPageAt(final long revision) {
        return readRevision.readPageAt(revision);
    }

    @Override
    public Page<Revision> readRevisions(final String id, final int limit, @Nullable final Long after) {
        return readRevision.readRevisions(id, limit, after);
    }

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
