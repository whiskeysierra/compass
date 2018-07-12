package org.zalando.compass.library.http;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Base64;

import static com.google.common.primitives.Longs.toByteArray;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@AllArgsConstructor
@Getter
public final class ETag {

    private static final Base64.Encoder ENCODE = Base64.getUrlEncoder().withoutPadding();

    long revision;

    @Override
    public String toString() {
        return Quoting.quote(encode(toByteArray(revision)));
    }

    public String encode(final byte[] bytes) {
        return ENCODE.encodeToString(bytes);
    }

}
