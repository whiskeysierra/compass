package org.zalando.compass.library.http;

import java.util.function.Function;
import java.util.function.Predicate;

public interface Condition<T> extends Predicate<T> {

    default <R> Predicate<R> onResultOf(final Function<R, T> function) {
        return input -> test(function.apply(input));
    }

}
