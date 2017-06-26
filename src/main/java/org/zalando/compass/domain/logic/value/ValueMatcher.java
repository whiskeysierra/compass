package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
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
        return value -> value.getDimensions().entrySet().stream().allMatch(entry -> {
            final RichDimension dimension = entry.getKey();
            final JsonNode configured = entry.getValue();

            @Nullable final JsonNode requested = filter.get(dimension);
            return requested != null && dimension.getRelation().test(configured, requested);
        });
    }

}
