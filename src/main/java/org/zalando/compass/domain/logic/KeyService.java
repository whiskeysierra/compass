package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Key;

import java.io.IOException;

public interface KeyService {

    boolean replace(Key key);

    void delete(String id);

}
