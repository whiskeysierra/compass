package org.zalando.compass.domain.model.event;

import lombok.Value;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;

@Value
public final class KeyReplaced {
    @Nullable
    Key before;
    Key after;
    Revision revision;
}
