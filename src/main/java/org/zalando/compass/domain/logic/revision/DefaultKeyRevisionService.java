package org.zalando.compass.domain.logic.revision;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.KeyRevisionService;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.revision.KeyRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
class DefaultKeyRevisionService implements KeyRevisionService {

    private final ReadKeyRevision readRevision;

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readPageRevisions(final Pagination<Long> query) {
        return readRevision.readPageRevisions(query);
    }

    @Transactional(readOnly = true)
    @Override
    public PageRevision<Key> readPageAt(final long revision, final Pagination<String> query) {
        return readRevision.readPageAt(revision, query);
    }

    @Transactional(readOnly = true)
    @Override
    public PageResult<Revision> readRevisions(final String id, final Pagination<Long> query) {
        return readRevision.readRevisions(id, query);
    }

    @Transactional(readOnly = true)
    @Override
    public KeyRevision readAt(final String id, final long revision) {
        return readRevision.readAt(id, revision);
    }

}
