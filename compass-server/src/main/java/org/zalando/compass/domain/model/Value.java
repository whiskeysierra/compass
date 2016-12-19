package org.zalando.compass.domain.model;

import com.google.common.collect.ImmutableMap;

@lombok.Value
public final class Value {

    private final ImmutableMap<String, Object> dimensions;
    private final Object value;

}
