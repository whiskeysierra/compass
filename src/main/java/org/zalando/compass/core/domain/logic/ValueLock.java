package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;

import javax.annotation.Nullable;
import java.util.Set;

@Getter
@AllArgsConstructor
class ValueLock {
    private final Set<Dimension> dimensions;
    private final Key key;
    @Nullable
    private final Value value;
}
