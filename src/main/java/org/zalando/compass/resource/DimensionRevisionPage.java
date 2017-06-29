package org.zalando.compass.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.DimensionRevision;

import java.util.List;

@Getter
@AllArgsConstructor
class DimensionRevisionPage {

    private final Link next;
    private final List<DimensionRevision> dimensions;

}
