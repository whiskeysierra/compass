package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.library.pagination.PageQuery;
import org.zalando.compass.library.pagination.PageResult;

import javax.annotation.Nullable;

public interface KeyService {

    boolean replace(Key key, @Nullable String comment);

    PageResult<Key> readPage(@Nullable String term, final PageQuery<String> query);

    Key read(String id);

    PageResult<Revision> readPageRevisions(final PageQuery<Long> query);

    PageRevision<Key> readPageAt(long revision, final PageQuery<String> query);

    PageResult<Revision> readRevisions(String id, final PageQuery<Long> query);

    KeyRevision readAt(String id, long revision);

    void delete(String id, @Nullable String comment);

}
