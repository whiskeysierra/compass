package org.zalando.compass.domain.model.event;

import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;

@lombok.Value
public class ValueReplaced {
    Key key;
    Value before;
    Value after;
    Revision revision;
}
