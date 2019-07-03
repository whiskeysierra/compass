package org.zalando.compass.revision.domain.spi.repository;

import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.library.pagination.Pagination;

import java.util.List;
import java.util.Optional;

public interface KeyRevisionRepository {
    void create(KeyRevision revision);

    List<Revision> findPageRevisions(Pagination<Long> query);

    List<Key> findPage(long revisionId, Pagination<String> query);

    List<Revision> findRevisions(String id, Pagination<Long> query);

    Optional<KeyRevision> find(String id, long revision);
}
