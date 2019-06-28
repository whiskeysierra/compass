package org.zalando.compass.infrastructure.resource;

import com.google.common.primitives.Longs;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Base64;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
final class ETag {

    private static final Base64.Encoder ENCODE = Base64.getUrlEncoder().withoutPadding();

    long revision;

    @Override
    public String toString() {
        return ENCODE.encodeToString(Longs.toByteArray(revision));
    }

}
