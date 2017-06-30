package org.zalando.compass.library;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.experimental.Wither;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public final class Maps {

    // TODO find better name
    @AllArgsConstructor
    @Getter
    @EqualsAndHashCode
    public static final class Pair<L, R> {

        @Wither
        private final L left;

        @Wither
        private final R right;

        private static <L, R> Pair<L, R> empty() {
            return new Pair<>(null, null);
        }

        private static <L, R> Pair<L, R> merge(final Pair<L, R> left, final Pair<L, R> right) {
            return new Pair<>(left.left, right.right);
        }

    }

    public static <K, V> Map<K, Pair<V, V>> diff(final Collection<V> lefts, final Collection<V> rights,
            final Function<V, K> toKey) {

        final Pair<V, V> pair = Pair.empty();
        final Map<K, Pair<V, V>> left = lefts.stream().collect(toMap(toKey, pair::withLeft));
        final Map<K, Pair<V, V>> right = rights.stream().collect(toMap(toKey, pair::withRight));

        right.forEach((k, v) -> left.merge(k, v, Pair::merge));

        return left;
    }

}
