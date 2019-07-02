package org.zalando.compass.core.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

public interface Dimensional {

    ImmutableMap<Dimension, JsonNode> getDimensions();

}
