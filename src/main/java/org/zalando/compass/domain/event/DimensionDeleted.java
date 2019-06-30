package org.zalando.compass.domain.event;

import lombok.Value;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.Revision;

@Value
public final class DimensionDeleted {
    Dimension dimension;
    Revision revision;
}
