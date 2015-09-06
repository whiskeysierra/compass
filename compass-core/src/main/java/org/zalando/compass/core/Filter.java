package org.zalando.compass.core;

import org.zalando.compass.api.DimensionId;
import org.zalando.compass.api.Values;

import java.util.Map;

public class Filter {

    Values fiter(final Values values, final Map<DimensionId, String> dimensions) {
        // TODO filter given values based on already given dimension
        // this should reduce values to a smaller set of entries
        // every given dimension should not be present anymore in the returned values
        return values;
    }

}
