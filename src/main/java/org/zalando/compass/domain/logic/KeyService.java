package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.PageRevision;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.util.List;

public interface KeyService {

    boolean replace(Key key, @Nullable String comment);

    // TODO Page
    List<Key> readPage(@Nullable String term);

    Key read(String id);

    Page<Revision> readPageRevisions(int limit, @Nullable Long after);

    PageRevision<Key> readPageAt(long revision);

    Page<Revision> readRevisions(String id, int limit, @Nullable Long after);

    KeyRevision readAt(String id, long revision);

    void delete(String id, @Nullable String comment);

}
