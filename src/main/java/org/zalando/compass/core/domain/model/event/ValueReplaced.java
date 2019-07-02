package org.zalando.compass.core.domain.model.event;

import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;

@lombok.Value
public class ValueReplaced {
    Key key;
    Value before;
    Value after;
    String comment;
}
