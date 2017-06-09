package org.zalando.compass.resource;

import org.zalando.compass.domain.model.Dimension;

import java.util.List;

@lombok.Value
public class DimensionPage {

    private final List<Dimension> dimensions;

}
