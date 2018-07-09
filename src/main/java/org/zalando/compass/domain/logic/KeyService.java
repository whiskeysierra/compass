package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.library.pagination.Pagination;

import javax.annotation.Nullable;

public interface KeyService {

    boolean replace(Key key, @Nullable String comment);

    void create(Key key, @Nullable String comment) throws EntityAlreadyExistsException;

    PageResult<Key> readPage(@Nullable String term, final Pagination<String> query);

    Key read(String id);

    PageResult<Revision> readPageRevisions(final Pagination<Long> query);

    PageRevision<Key> readPageAt(long revision, final Pagination<String> query);

    PageResult<Revision> readRevisions(String id, final Pagination<Long> query);

    KeyRevision readAt(String id, long revision);

    void delete(String id, @Nullable String comment);

}
