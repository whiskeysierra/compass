package org.zalando.compass.core.domain.logic;

import java.util.function.Function;

final class Changed {

    private Changed() {

    }

    static <T, P> boolean changed(final Function<T, P> function, final T previous, final T next) {
        return !function.apply(previous).equals(function.apply(next));
    }

}
