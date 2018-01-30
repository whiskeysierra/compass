package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;

public interface DimensionService {

    boolean replace(Dimension dimension, @Nullable String comment);

    PageResult<Dimension> readPage(@Nullable String term, final Pagination<String> query);

    Dimension read(String id);

    PageResult<Revision> readPageRevisions(final Pagination<Long> query);

    PageRevision<Dimension> readPageAt(long revision, final Pagination<String> query);

    PageResult<Revision> readRevisions(String id, final Pagination<Long> query);

    DimensionRevision readAt(String id, long revision);

    void delete(String id, @Nullable String comment);

}
