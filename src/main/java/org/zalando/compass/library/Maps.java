package org.zalando.compass.library;

import com.google.common.collect.ImmutableMap;

import java.util.Map;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.toImmutableMap;
import static java.util.function.Function.identity;

public final class Maps {

    private Maps() {

    }

    public static <T, R, V> ImmutableMap<R, V> transform(final Map<T, V> map, final Function<T, R> keyFunction) {
        return transform(map, keyFunction, identity());
    }

    public static <T, R, V, U> ImmutableMap<R, U> transform(final Map<T, V> map, final Function<T, R> keyFunction,
            final Function<V, U> valueFunction) {
        return map.entrySet().stream()
                .collect(toImmutableMap(e -> keyFunction.apply(e.getKey()), e -> valueFunction.apply(e.getValue())));
    }

}
