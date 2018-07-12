package org.zalando.compass.library.http;

import javax.annotation.Nullable;
import java.util.function.Function;

public final class Conditions<T> implements Condition<T> {

    private final Condition<T> condition;

    private Conditions(final Condition<T> condition) {
        this.condition = condition;
    }

    public <R> Conditions<T> or(@Nullable final Condition<R> next, final Function<T, R> function) {
        if (next == null) {
            return this;
        }

        return condition == null ? this : new Conditions<>(x -> next.test(function.apply(x)));
    }

    @Override
    public boolean test(final T input) {
        return condition == null || condition.test(input);
    }

    public static <T> Conditions<T> empty() {
        return new Conditions<>(null);
    }

}
