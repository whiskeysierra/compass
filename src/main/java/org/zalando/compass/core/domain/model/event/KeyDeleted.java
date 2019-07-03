package org.zalando.compass.core.domain.model.event;

import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;

import java.util.List;

@lombok.Value
public final class KeyDeleted implements Event {
    Key key;
    List<Value> values;
    String comment;
}
