package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Key;

import javax.annotation.Nullable;
import java.util.List;

public interface KeyService {

    boolean replace(Key key);

    Key read(String id);

    List<Key> readAll(@Nullable String term);

    void delete(String id);

}
