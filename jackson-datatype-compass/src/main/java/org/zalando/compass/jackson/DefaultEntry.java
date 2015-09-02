package org.zalando.compass.jackson;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.common.collect.ImmutableMap;
import org.zalando.compass.api.Dimension;
import org.zalando.compass.api.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static com.google.common.base.MoreObjects.firstNonNull;

final class DefaultEntry<T> implements Entry<T> {

    private final ImmutableMap<Dimension, String> dimensions;
    private final T value;

    @JsonCreator
    DefaultEntry(
            @JsonProperty(value = "dimensions", required = false)
            @JsonDeserialize(keyAs = DefaultDimension.class)
            @Nullable final ImmutableMap<Dimension, String> dimensions,
            @JsonProperty(value = "value", required = true) final T value) {
        this.dimensions = firstNonNull(dimensions, ImmutableMap.of());
        this.value = value;
    }

    @JsonProperty("dimensions")
    @Override
    public ImmutableMap<Dimension, String> getDimensions() {
        return dimensions;
    }

    @JsonProperty("value")
    @Nonnull
    @Override
    public T getValue() {
        return value;
    }

}
