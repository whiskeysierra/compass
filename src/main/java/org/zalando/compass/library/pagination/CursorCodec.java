package org.zalando.compass.library.pagination;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nullable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;

import static java.nio.charset.StandardCharsets.UTF_8;

@Slf4j
final class CursorCodec {

    static final CursorCodec CODEC = new CursorCodec();

    private final ObjectMapper mapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_MISSING_CREATOR_PROPERTIES)
            .enable(DeserializationFeature.USE_LONG_FOR_INTS)
            .addMixIn(Cursor.class, CursorMixIn.class)
            .addMixIn(Direction.class, DirectionMixIn.class);

    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
    private final Base64.Decoder decoder = Base64.getUrlDecoder();

    @Nullable
    <P> String encode(final Cursor<P> cursor) {
        try {
            return toBase64(toJSON(cursor));
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    <P> Cursor<P> decode(final String cursor) {
        if (cursor.isEmpty()) {
            return Cursor.create(null, null);
        }

        try {
            return fromJSON(fromBase64(cursor));
        } catch (final Exception e) {
            log.warn("Received cursor '{}' is invalid, ignoring it", cursor, e);
            return Cursor.create(null, null);
        }
    }

    private <P> String toJSON(final Cursor<P> cursor) throws JsonProcessingException {
        return mapper.writeValueAsString(cursor);
    }

    @SuppressWarnings("unchecked")
    private <P> Cursor<P> fromJSON(final String json) throws IOException {
        return mapper.readValue(json, Cursor.class);
    }

    private String toBase64(final String json) {
        return encoder.encodeToString(json.getBytes(UTF_8));
    }

    private String fromBase64(final String cursor) {
        return new String(decoder.decode(cursor), UTF_8);
    }

}
