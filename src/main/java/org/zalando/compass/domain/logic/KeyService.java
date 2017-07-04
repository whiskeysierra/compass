package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyRevision;
import org.zalando.compass.domain.model.Page;

import javax.annotation.Nullable;
import java.util.List;

public interface KeyService {

    boolean replace(Key key);

    List<Key> readAll(@Nullable String term);

    Key read(String id);

    Page<KeyRevision> readRevisions(String id, int limit, @Nullable Long after);

    KeyRevision readRevision(String id, long revision);

    void delete(String id);

}
