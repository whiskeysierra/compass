package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Key;

import java.io.IOException;
import java.util.List;

public interface KeyService {

    boolean replace(Key key);

    Key read(String id);

    List<Key> readAll();

    void delete(String id);

}
