package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Service
class ValueMatcher {

    List<RichValue> match(final List<RichValue> values, final Map<RichDimension, JsonNode> filter) {
        return values.stream().filter(matcher(filter)).collect(toList());
    }

    private Predicate<RichValue> matcher(final Map<RichDimension, JsonNode> filter) {
        return value -> {
            final ImmutableMap<RichDimension, JsonNode> dimensions = value.getDimensions();

            return dimensions.entrySet().stream()
                    .allMatch(e -> {
                        final RichDimension dimension = e.getKey();
                        final JsonNode configured = e.getValue();
                        @Nullable final JsonNode requested = filter.get(dimension);
                        return requested != null && dimension.getRelation().test(configured, requested);
                    });
        };
    }

}
