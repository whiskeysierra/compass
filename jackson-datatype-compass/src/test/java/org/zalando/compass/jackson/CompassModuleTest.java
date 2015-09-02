package org.zalando.compass.jackson;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import org.junit.Test;
import org.zalando.compass.api.Entry;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import static com.google.common.io.Resources.getResource;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertThat;

public final class CompassModuleTest {

    private final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new CompassModule())
            .registerModule(new GuavaModule());
    
    @Test
    public void shouldDeserializeFlatTree() throws IOException {
        final List<Entry<BigDecimal>> entries = mapper.readValue(getResource("flat.json"), listOfEntries());

        assertThat(entries, hasSize(1));
        assertThat(entries.get(0).getValue(), comparesEqualTo(new BigDecimal("0.19")));
    }

    @Test
    public void shouldDeserializeOneDimensionalTree() throws IOException {
        final List<Entry<BigDecimal>> entries = mapper.readValue(getResource("one-dimensional.json"), listOfEntries());

        assertThat(entries, hasSize(2));
    }

    @Test
    public void shouldDeserializeMultiDimensionalTree() throws IOException {
        final List<Entry<BigDecimal>> entries = mapper.readValue(getResource("multi-dimensional.json"), listOfEntries());

        assertThat(entries, hasSize(17));
    }

    private TypeReference<List<Entry<BigDecimal>>> listOfEntries() {
        return new TypeReference<List<Entry<BigDecimal>>>() {
        };
    }
    
}