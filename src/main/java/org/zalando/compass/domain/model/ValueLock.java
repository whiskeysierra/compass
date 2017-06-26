package org.zalando.compass.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@AllArgsConstructor
public class ValueLock {
    private final List<Dimension> dimensions;
    private final Key key;
    @Nullable
    private final Value value;
}
