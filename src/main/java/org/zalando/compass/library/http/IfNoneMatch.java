package org.zalando.compass.library.http;

import org.zalando.compass.domain.logic.BadArgumentException;

import javax.annotation.Nullable;
import java.util.Collection;

public abstract class IfNoneMatch implements Condition<String> {

    private IfNoneMatch() {
        // no extensible from the outside
    }

    private static final class None extends IfNoneMatch {
        @Override
        public boolean test(final String eTag) {
            return eTag == null;
        }
    }

    @Nullable
    public static Condition<String> valueOf(final Collection<String> values) {
        if (values.isEmpty()) {
            return null;
        }

        if ("*".equals(value)) {
            return new None();
        }

        throw new BadArgumentException("If-None-Match: " + value);
    }

}
