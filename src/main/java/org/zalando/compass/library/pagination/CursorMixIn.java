package org.zalando.compass.library.pagination;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, property = "d")
@JsonSubTypes({
        @JsonSubTypes.Type(name = ">", value = ForwardCursor.class),
        @JsonSubTypes.Type(name = "<", value = BackwardCursor.class)
})
abstract class CursorMixIn {
}
