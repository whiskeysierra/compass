package org.zalando.compass.library;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QueryFilter {

    private final Collection<String> excluded;

    public QueryFilter(final Collection<String> excluded) {
        this.excluded = excluded;
    }

    public <V> Map<String, V> filter(final Map<String, V> query) {
        final Map<String, V> copy = new LinkedHashMap<>(query);
        copy.keySet().removeAll(excluded);
        return copy;
    }

}
