package org.zalando.compass.kernel.domain.model.event;

import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.kernel.domain.model.Value;

@lombok.Value
public class ValueReplaced {
    Key key;
    Value before;
    Value after;
    String comment;
}
