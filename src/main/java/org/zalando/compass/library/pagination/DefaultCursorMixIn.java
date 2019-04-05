package org.zalando.compass.library.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

import javax.annotation.Nullable;
import java.util.Map;

@SuppressWarnings("unused")
@JsonPropertyOrder({"d", "p", "q"})
abstract class DefaultCursorMixIn<P> {

    @JsonCreator
    DefaultCursorMixIn(
            @JsonProperty("d") final Direction direction,
            @JsonProperty("p") final P pivot,
            @JsonProperty("q") final Map<String, String> query) {
    }

    @JsonProperty("d")
    abstract Direction getDirection();

    @JsonProperty("p")
    abstract P getPivot();

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty("q")
    abstract Map<String, String> getQuery();

}
