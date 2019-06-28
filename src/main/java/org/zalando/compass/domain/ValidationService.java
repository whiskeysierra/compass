package org.zalando.compass.domain;

import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;

import java.util.Collection;

public interface ValidationService {

    void check(Dimension dimension, Collection<Value> values);

    void check(Collection<Dimension> dimensions, Collection<Value> values);

    void check(Collection<Dimension> dimensions, Value value);

    void check(Key key, Collection<Value> values);

    void check(Key key, Value value);

}
