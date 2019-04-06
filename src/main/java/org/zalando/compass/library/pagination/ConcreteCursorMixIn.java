package org.zalando.compass.library.pagination;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@SuppressWarnings("unused")
@JsonPropertyOrder({"d", "p", "q", "l"})
abstract class ConcreteCursorMixIn<P, Q> {

    @JsonCreator
    ConcreteCursorMixIn(
            @JsonProperty("p") final P pivot,
            @JsonProperty("q") final Q query,
            @JsonProperty("l") final int limit) {
    }

    @JsonProperty("p")
    abstract P getPivot();

    @JsonInclude(Include.NON_EMPTY)
    @JsonProperty("q")
    abstract Q getQuery();

    @JsonProperty("l")
    abstract int getLimit();

}
