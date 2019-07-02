package org.zalando.compass.core.domain.model.event;

import lombok.Value;
import org.zalando.compass.core.domain.model.Key;

@Value
public final class KeyCreated {
    Key key;
    String comment;
}
