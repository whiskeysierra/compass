package org.zalando.compass.revision.domain.api;

import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.revision.domain.model.DimensionRevision;
import org.zalando.compass.core.domain.model.PageRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

public interface DimensionRevisionService {

    void create(DimensionRevision revision);

    PageResult<Revision> readPageRevisions(Pagination<Long> query);

    PageRevision<Dimension> readPageAt(long revision, Pagination<String> query);

    PageResult<Revision> readRevisions(String id, Pagination<Long> query);

    DimensionRevision readAt(String id, long revision);

}
