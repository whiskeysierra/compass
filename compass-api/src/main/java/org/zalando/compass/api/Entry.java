package org.zalando.compass.api;

import com.google.common.collect.ImmutableMap;

import javax.annotation.Nonnull;
import javax.annotation.concurrent.Immutable;

@Immutable
public interface Entry<T> {

    ImmutableMap<DimensionId, String> getDimensions();

    @Nonnull
    T getValue();

}
