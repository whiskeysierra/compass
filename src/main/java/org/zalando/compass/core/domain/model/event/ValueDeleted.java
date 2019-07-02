package org.zalando.compass.core.domain.model.event;

import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Value;

@lombok.Value
public class ValueDeleted {
    Key key;
    Value value;
    String comment;
}
