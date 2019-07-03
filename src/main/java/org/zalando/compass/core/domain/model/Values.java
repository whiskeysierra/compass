package org.zalando.compass.core.domain.model;

import com.google.common.collect.ForwardingList;
import com.google.common.collect.ImmutableList;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public final class Values extends ForwardingList<Value> implements Matchable<Value> {

    @Getter
    private final ImmutableList<Value> values;

    public Values(final Value... values) {
        this(ImmutableList.copyOf(values));
    }

    @Override
    protected List<Value> delegate() {
        return values;
    }

}
