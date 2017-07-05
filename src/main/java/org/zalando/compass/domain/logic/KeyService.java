package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;
import java.util.List;

public interface KeyService {

    boolean replace(Key key);

    // TODO Page
    List<Key> readAll(@Nullable String term);

    Key read(String id);

    Page<Revision> readRevisions(int limit, @Nullable Long after);

    // TODO Page
    List<Key> readRevision(long revision);

    Page<Revision> readRevisions(String id, int limit, @Nullable Long after);

    KeyRevision readRevision(String id, long revision);

    void delete(String id);

}
