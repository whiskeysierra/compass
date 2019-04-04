package org.zalando.compass.library.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;

@JsonSubTypes(@Type(DefaultCursor.class))
@JsonPropertyOrder({"d", "p"})
interface CursorMixIn<P> extends Cursor<P> {

    @JsonProperty("d")
    @Override
    Direction getDirection();

    @JsonProperty("p")
    @Override
    P getPivot();


    @JsonCreator
    static <P> Cursor<P> create(@JsonProperty("d") final Direction direction, @JsonProperty("p") final P pivot) {
        throw new UnsupportedOperationException();
    }

}
