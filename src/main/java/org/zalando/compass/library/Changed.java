package org.zalando.compass.library;

import java.util.function.Function;

public final class Changed {

    private Changed() {

    }

    public static <T, P> boolean changed(final Function<T, P> function, final T previous, final T next) {
        return !function.apply(previous).equals(function.apply(next));
    }

}
