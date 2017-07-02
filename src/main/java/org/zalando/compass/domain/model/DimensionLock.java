package org.zalando.compass.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@AllArgsConstructor
public class DimensionLock {
    @Nullable
    private final Dimension dimension;
    private final List<Value> values;
}
