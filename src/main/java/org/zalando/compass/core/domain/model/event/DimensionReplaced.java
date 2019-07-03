package org.zalando.compass.core.domain.model.event;

import lombok.Value;
import org.zalando.compass.core.domain.model.Dimension;

import javax.annotation.Nullable;

@Value
public final class DimensionReplaced implements Event {
    @Nullable
    Dimension before;
    Dimension after;
    String comment;
}
