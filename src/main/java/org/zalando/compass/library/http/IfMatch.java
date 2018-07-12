package org.zalando.compass.library.http;

import org.zalando.compass.domain.logic.BadArgumentException;
import org.zalando.compass.domain.model.Revisioned;

import java.util.Collection;
import java.util.function.Predicate;

public abstract class IfMatch implements Condition<String> {

    private IfMatch() {
        // no extensible from the outside
    }

    private static final class Any extends IfMatch {
        @Override
        public boolean test(final String eTag) {
            return true;
        }
    }

    public static Condition<String> valueOf(final Collection<String> values) {
        if (values.isEmpty()) {
            return null;
        }

        if ("*".equals(value)) {
            return new Any();
        }

        return new IfMatch() {
            @Override
            public boolean test(final String eTag) {
                return
            }
        }
    }

}
