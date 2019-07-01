package org.zalando.compass.kernel.domain.model.event;

import lombok.Value;
import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.kernel.domain.model.Revision;

@Value
public final class DimensionDeleted {
    Dimension dimension;
    Revision revision;
}
