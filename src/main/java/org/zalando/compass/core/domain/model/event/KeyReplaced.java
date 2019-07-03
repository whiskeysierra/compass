package org.zalando.compass.core.domain.model.event;

import lombok.Value;
import org.zalando.compass.core.domain.model.Key;

import javax.annotation.Nullable;

@Value
public final class KeyReplaced implements Event {
    @Nullable
    Key before;
    Key after;
    String comment;
}
