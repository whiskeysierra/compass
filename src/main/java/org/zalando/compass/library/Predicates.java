package org.zalando.compass.library;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Predicate;

public final class Predicates {

    private Predicates() {

    }

    @SafeVarargs
    public static <T> Predicate<T> or(final Predicate<T>... predicates) {
        return or(Arrays.asList(predicates));
    }

    public static <T> Predicate<T> or(final Collection<Predicate<T>> predicates) {
        return predicates.stream()
                .reduce(Predicate::or)
                .orElse(x -> false);
    }

}
