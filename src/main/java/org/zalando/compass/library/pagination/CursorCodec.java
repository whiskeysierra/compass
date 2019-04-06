package org.zalando.compass.library.pagination;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.Type;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@SuppressWarnings("UnstableApiUsage") // TypeToken
@Slf4j
final class CursorCodec {

    static final CursorCodec CODEC = new CursorCodec();

    private final ObjectMapper mapper = new ObjectMapper()
            .addMixIn(Cursor.class, CursorMixIn.class)
            .addMixIn(ForwardCursor.class, ConcreteCursorMixIn.class)
            .addMixIn(BackwardCursor.class, ConcreteCursorMixIn.class);

    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder decoder = Base64.getUrlDecoder();

    @Nullable
    <P, Q> String encode(final Cursor<P, Q> cursor) {
        try {
            return toBase64(toJSON(cursor));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    <P, Q> Cursor<P, Q> decode(final String cursor, final TypeToken<P> pivot, final TypeToken<Q> query) {
        if (cursor.isEmpty()) {
            return Cursor.initial();
        }

        final TypeToken<Cursor<P, Q>> cursorType = new TypeToken<Cursor<P, Q>>() {
            // nothing to implement!
        };
        final TypeParameter<P> pivotType = new TypeParameter<P>() {
            // nothing to implement!
        };
        final TypeParameter<Q> queryType = new TypeParameter<Q>() {
            // nothing to implement!
        };

        try {
            return fromJSON(fromBase64(cursor), cursorType
                    .where(pivotType, pivot)
                    .where(queryType, query));
        } catch (final Exception e) {
            log.warn("Received cursor '{}' is invalid, ignoring it", cursor, e);
            return Cursor.initial();
        }
    }

    private <P, Q> String toJSON(final Cursor<P, Q> cursor) throws JsonProcessingException {
        return mapper.writeValueAsString(cursor);
    }

    private <P, Q> Cursor<P, Q> fromJSON(final String json, final TypeToken<Cursor<P, Q>> type) throws IOException {
        return mapper.readValue(json, new TypeReference<Cursor<P, Q>>() {
            @Override
            public Type getType() {
                return type.getType();
            }
        });
    }

    private String toBase64(final String json) {
        return encoder.encodeToString(json.getBytes(UTF_8));
    }

    private String fromBase64(final String cursor) {
        return new String(decoder.decode(cursor), UTF_8);
    }

}
