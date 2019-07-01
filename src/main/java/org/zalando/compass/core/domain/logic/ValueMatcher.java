package org.zalando.compass.core.domain.logic;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

@Service
class ValueMatcher {

    List<Map<RichDimension, JsonNode>> match(final Collection<Map<RichDimension, JsonNode>> values,
            final Map<RichDimension, JsonNode> filter) {
        return values.stream().filter(matcher(filter)).collect(toList());
    }

    private Predicate<Map<RichDimension, JsonNode>> matcher(final Map<RichDimension, JsonNode> filter) {
        return dimensions -> dimensions.entrySet().stream().allMatch(entry -> {
            final RichDimension dimension = entry.getKey();
            final JsonNode configured = entry.getValue();

            @Nullable final JsonNode requested = filter.get(dimension);
            return requested != null && dimension.getRelation().test(configured, requested);
        });
    }

}
