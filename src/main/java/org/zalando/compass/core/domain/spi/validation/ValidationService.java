package org.zalando.compass.core.domain.spi.validation;

import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Value;

import java.util.Collection;

public interface ValidationService {

    void check(Dimension dimension, Collection<Value> values);

    void check(Collection<Dimension> dimensions, Collection<Value> values);

    void check(Collection<Dimension> dimensions, Value value);

    void check(Key key, Collection<Value> values);

    void check(Key key, Value value);

}
