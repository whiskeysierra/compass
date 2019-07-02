package org.zalando.compass.kernel.domain.model.event;

import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Value;

import java.util.List;

@lombok.Value
public final class KeyDeleted {
    Key key;
    List<Value> values;
    String comment;
}
