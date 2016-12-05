package org.zalando.compass.spi;

import java.util.Comparator;

public interface Operator<T> extends Comparator<T> {

    String getName();
    
}
