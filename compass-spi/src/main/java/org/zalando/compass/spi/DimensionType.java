package org.zalando.compass.spi;

import java.util.List;

public interface DimensionType<T> {

    String getName();
    
    T parse(String value);
    
    void validate(final List<T> values);
    
    T select(final List<T> values, final T value);

}
