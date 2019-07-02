package org.zalando.compass.core.domain.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;

import java.util.List;
import java.util.Set;

@Getter
@AllArgsConstructor
class ValuesLock {
    private final Set<Dimension> dimensions;
    private final Key key;
    private final List<Value> values;
}
