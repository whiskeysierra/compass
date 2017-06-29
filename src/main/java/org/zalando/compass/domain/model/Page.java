package org.zalando.compass.domain.model;

import java.util.List;

@lombok.Value
public class Page<T> {

    List<T> elements;
    T next;

}
