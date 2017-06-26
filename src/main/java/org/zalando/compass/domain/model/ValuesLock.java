package org.zalando.compass.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ValuesLock {
    private final List<Dimension> dimensions;
    private final Key key;
    private final List<Value> values;
}
