package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Value;

import java.util.List;

@Getter
@AllArgsConstructor
class ValuesLock {
    private final List<Dimension> dimensions;
    private final Key key;
    private final List<Value> values;
}
