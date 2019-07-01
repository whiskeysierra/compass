package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.kernel.domain.model.Value;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@AllArgsConstructor
class DimensionLock {
    @Nullable
    private final Dimension dimension;
    private final List<Value> values;
}
