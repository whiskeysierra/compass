package org.zalando.compass.revision.domain.api;

import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.kernel.domain.model.PageRevision;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

public interface KeyRevisionService {
    PageResult<Revision> readPageRevisions(Pagination<Long> query);

    PageRevision<Key> readPageAt(long revision, Pagination<String> query);

    PageResult<Revision> readRevisions(String id, Pagination<Long> query);

    KeyRevision readAt(String id, long revision);
}
