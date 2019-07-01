package org.zalando.compass.kernel.domain.model.event;

import lombok.Value;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.kernel.domain.model.Revision;

@Value
public final class KeyDeleted {
    Key key;
    Revision revision;
}
