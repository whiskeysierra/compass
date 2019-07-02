package org.zalando.compass.core.domain.model;

import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;

@lombok.Value
@AllArgsConstructor
public final class Values implements Matchable<Value> {

    ImmutableList<Value> values;

    public Values(final Value... values) {
        this(ImmutableList.copyOf(values));
    }

}
