package org.zalando.compass.library;

import org.jooq.Field;
import org.jooq.impl.DSL;

import javax.annotation.Nullable;

public final class Seek {

    public static <T> Field<T> field(@Nullable final T value, final Class<T> type) {
        return value == null ? null : DSL.val(value, type);
    }

}
