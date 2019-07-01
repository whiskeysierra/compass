package org.zalando.compass.domain.model.event;

import lombok.Value;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Revision;

@Value
public final class KeyCreated {
    Key key;
    Revision revision;
}
