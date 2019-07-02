package org.zalando.compass.kernel.domain.model.event;

import lombok.Value;
import org.zalando.compass.kernel.domain.model.Key;

@Value
public final class KeyCreated {
    Key key;
    String comment;
}
