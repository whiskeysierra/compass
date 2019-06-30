package org.zalando.compass.domain.event;

import lombok.Value;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Revision;

import javax.annotation.Nullable;

@Value
public final class DimensionReplaced {
    @Nullable
    Dimension before;
    Dimension after;
    Revision revision;
}
