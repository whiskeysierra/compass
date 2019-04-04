package org.zalando.compass.library.pagination;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.util.StdConverter;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;

@JsonSerialize(converter = DirectionMixIn.DirectionToStringConverter.class)
@JsonDeserialize(converter = DirectionMixIn.StringToDirectionConverter.class)
interface DirectionMixIn {

    BiMap<Direction, String> DIRECTIONS = ImmutableBiMap.of(
            Direction.FORWARD, ">",
            Direction.BACKWARD, "<"
    );

    final class DirectionToStringConverter extends StdConverter<Direction, String> {
        @Override
        public String convert(final Direction value) {
            return DIRECTIONS.get(value);
        }
    }

    final class StringToDirectionConverter extends StdConverter<String, Direction> {
        @Override
        public Direction convert(final String value) {
            return DIRECTIONS.inverse().get(value);
        }
    }

}
