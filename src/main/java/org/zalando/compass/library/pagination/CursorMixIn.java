package org.zalando.compass.library.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Map;

@JsonSubTypes(@Type(DefaultCursor.class))
@JsonPropertyOrder({"d", "p", "q"})
interface CursorMixIn<P> extends Cursor<P> {

    @JsonProperty("d")
    @Override
    Direction getDirection();

    @JsonProperty("p")
    @Override
    P getPivot();

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty("q")
    @Override
    Map<String, String> getQuery();

    @JsonCreator
    static <P> Cursor<P> create(
            @JsonProperty("d") final Direction direction,
            @JsonProperty("p") final P pivot,
            @JsonProperty("q") final Map<String, JsonNode> query) {
        throw new UnsupportedOperationException();
    }

}
