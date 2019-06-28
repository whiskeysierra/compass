package org.zalando.compass.domain.repository;

import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.Pagination;

import java.util.List;
import java.util.Optional;

public interface DimensionRevisionRepository {
    void create(DimensionRevision dimension);

    List<Revision> findPageRevisions(Pagination<Long> query);

    List<Dimension> findPage(long revisionId, Pagination<String> query);

    List<Revision> findRevisions(String id, Pagination<Long> query);

    Optional<DimensionRevision> find(String id, long revision);
}
