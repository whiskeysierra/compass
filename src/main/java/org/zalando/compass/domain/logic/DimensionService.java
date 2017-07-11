package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageQuery;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;

public interface DimensionService {

    boolean replace(Dimension dimension, @Nullable String comment);

    PageResult<Dimension> readPage(@Nullable String term, final PageQuery<String> query);

    Dimension read(String id);

    PageResult<Revision> readPageRevisions(final PageQuery<Long> query);

    PageRevision<Dimension> readPageAt(long revision, final PageQuery<String> query);

    PageResult<Revision> readRevisions(String id, final PageQuery<Long> query);

    DimensionRevision readAt(String id, long revision);

    void delete(String id, @Nullable String comment);

}
