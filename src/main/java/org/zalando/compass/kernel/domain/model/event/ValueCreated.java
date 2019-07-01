package org.zalando.compass.kernel.domain.model.event;

import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.kernel.domain.model.Value;

@lombok.Value
public class ValueCreated {
    Key key;
    Value value;
    Revision revision;
}
