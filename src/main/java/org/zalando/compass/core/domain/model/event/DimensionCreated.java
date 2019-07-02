package org.zalando.compass.core.domain.model.event;

import lombok.Value;
import org.zalando.compass.core.domain.model.Dimension;

@Value
public final class DimensionCreated {
    Dimension dimension;
    String comment;
}
