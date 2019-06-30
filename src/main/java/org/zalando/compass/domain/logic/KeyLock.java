package org.zalando.compass.domain.logic;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Value;

import javax.annotation.Nullable;
import java.util.List;

@Getter
@AllArgsConstructor
class KeyLock {
    @Nullable
    private final Key key;
    private final List<Value> values;
}
