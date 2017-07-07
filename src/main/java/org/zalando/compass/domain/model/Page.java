package org.zalando.compass.domain.model;

import lombok.experimental.Wither;

import java.util.List;

@lombok.Value
public class Page<T> {

    @Wither
    List<T> elements;
    T next;

}
