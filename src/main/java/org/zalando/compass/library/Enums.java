package org.zalando.compass.library;

public final class Enums {

    public static <A extends Enum<A>, B extends Enum<B>> B translate(final A value, final Class<B> type) {
        return Enum.valueOf(type, value.name());
    }

}
