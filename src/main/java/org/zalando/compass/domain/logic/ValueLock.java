package org.zalando.compass.domain.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@AllArgsConstructor
class ValueLock {
    private final List<Dimension> dimensions;
    private final Key key;
    @Nullable
    private final Value value;
}
