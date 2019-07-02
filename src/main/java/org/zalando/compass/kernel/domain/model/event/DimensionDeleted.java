package org.zalando.compass.kernel.domain.model.event;

import lombok.Value;
import org.zalando.compass.kernel.domain.model.Dimension;

@Value
public final class DimensionDeleted {
    Dimension dimension;
    String comment;
}
