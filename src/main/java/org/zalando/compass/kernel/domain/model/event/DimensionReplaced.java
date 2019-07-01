package org.zalando.compass.kernel.domain.model.event;

import lombok.Value;
import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.kernel.domain.model.Revision;

import javax.annotation.Nullable;

@Value
public final class DimensionReplaced {
    @Nullable
    Dimension before;
    Dimension after;
    Revision revision;
}
