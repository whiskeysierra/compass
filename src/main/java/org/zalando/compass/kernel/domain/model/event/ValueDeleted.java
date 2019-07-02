package org.zalando.compass.kernel.domain.model.event;

import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Value;

@lombok.Value
public class ValueDeleted {
    Key key;
    Value value;
    String comment;
}
