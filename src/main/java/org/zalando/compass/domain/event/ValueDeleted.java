package org.zalando.compass.domain.event;

import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Revision;
import org.zalando.compass.domain.model.Value;

@lombok.Value
public class ValueDeleted {
    Key key;
    Value value;
    Revision revision;
}
