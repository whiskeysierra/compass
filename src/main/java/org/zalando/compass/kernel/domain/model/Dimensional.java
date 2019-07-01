package org.zalando.compass.kernel.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

public interface Dimensional {

    ImmutableMap<String, JsonNode> getDimensions();

}
