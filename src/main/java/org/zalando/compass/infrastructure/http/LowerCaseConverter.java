package org.zalando.compass.infrastructure.http;

import com.fasterxml.jackson.databind.util.StdConverter;

import java.util.Locale;

public class LowerCaseConverter<E extends Enum<E>> extends StdConverter<E, String> {

    @Override
    public String convert(final E value) {
        return value.name().toLowerCase(Locale.ROOT);
    }

}
