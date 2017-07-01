package org.zalando.compass.domain.model;

import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

@Getter
@AllArgsConstructor
public class DimensionLock {
    @Nullable
    private final Dimension dimension;
    private final Multimap<String, Value> values;
}
