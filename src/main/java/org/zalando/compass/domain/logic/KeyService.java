package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;

public interface KeyService {

    boolean replace(Key key, @Nullable String comment);

    PageResult<Key> readPage(@Nullable String term, final int limit, @Nullable final String after);

    Key read(String id);

    PageResult<Revision> readPageRevisions(int limit, @Nullable Long after);

    PageRevision<Key> readPageAt(long revision, final int limit, @Nullable final String after);

    PageResult<Revision> readRevisions(String id, int limit, @Nullable Long after);

    KeyRevision readAt(String id, long revision);

    void delete(String id, @Nullable String comment);

}
