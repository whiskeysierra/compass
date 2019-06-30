package org.zalando.compass.domain.logic.revision;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.DimensionRevisionService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.revision.DimensionRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DefaultDimensionRevisionService implements DimensionRevisionService {

    private final ReadDimensionRevision readRevision;

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

}
