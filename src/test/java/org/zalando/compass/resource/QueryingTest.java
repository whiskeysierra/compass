package org.zalando.compass.resource;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Map;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.library.JsonConfiguration.jacksonObjectMapper;

public final class QueryingTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final Querying unit = new Querying(jacksonObjectMapper());

    public QueryingTest() throws NoSuchMethodException {
        // needed because QueryService constructor throws (doesn't really, but the compiler doesn't know that)
    }

    @Test
    public void shouldReadEmpty() {
        assertThat(unit.read(emptyMap()), is(emptyMap()));
    }

    @Test
    public void shouldReadStrings() {
        assertThat(unit.read(map("after", "2017-06-08T15:32:00Z")), is(map("after", text("2017-06-08T15:32:00Z"))));
    }

    @Test
    public void shouldReadQuotedStrings() {
        assertThat(unit.read(map("test", "\"true\"")), is(map("test", text("true"))));
    }

    @Test
    public void shouldReadStringsWithColon() {
        assertThat(unit.read(map("test", "1:0")), is(map("test", text("1:0"))));
    }

    @Test
    public void shouldReadIntegerNumbers() {
        assertThat(unit.read(map("quantity", "123")), is(map("quantity", new BigIntegerNode(new BigInteger("123")))));
    }

    @Test
    public void shouldReadDecimalNumbers() {
        assertThat(unit.read(map("rate", "0.1")), is(map("rate", new DecimalNode(new BigDecimal("0.1")))));
    }

    @Test
    public void shouldReadBooleans() {
        assertThat(unit.read(map("active", "true")), is(map("active", BooleanNode.TRUE)));
    }

    @Test
    public void shouldReadObjects() {
        assertThat(unit.read(map("price", "{\"amount\":25.0}")), is(map("price", new ObjectNode(instance,
                map("amount", new DecimalNode(new BigDecimal(25.0)))))));
    }

    @Test
    public void shouldReadArrays() {
        assertThat(unit.read(map("users", "[\"alice\",\"bob\"]")), is(map("users", new ArrayNode(instance,
                Arrays.asList(new TextNode("alice"), new TextNode("bob"))))));
    }

    @Test
    public void shouldReadEmptyStringsAsMissing() {
        assertThat(unit.read(map("country", "")), is(map("country", MissingNode.getInstance())));
    }

    @Test
    public void shouldReadBlankStringsAsMissing() {
        assertThat(unit.read(map("country", " ")), is(map("country", MissingNode.getInstance())));
    }

    @Test
    public void shouldReadAbsentAsMissing() {
        assertThat(unit.read(map("country", null)), is(map("country", MissingNode.getInstance())));
    }

    @Test
    public void shouldReadNulls() {
        assertThat(unit.read(map("country", "null")), is(map("country", NullNode.getInstance())));
    }

    @Test
    public void shouldThrowOriginalExceptionIfFallbackToStringDidntWork() {
        exception.expect(JsonParseException.class);
        exception.expectMessage("Unrecognized token 'A'");

        unit.read(map("test", "A\tB"));
    }

    @Test
    public void shouldIgnoreQueryWithoutReservedKeys() {
        assertThat(unit.read(singletonMap("foo", "bar")), is(singletonMap("foo", text("bar"))));
    }

    @Test
    public void shouldPartiallyFilterQueryWithReservedKeys() {
        assertThat(unit.read(map("foo", "bar", "limit", "1")), is(singletonMap("foo", text("bar"))));
    }

    @Test
    public void shouldFullyFilterQueryWithReservedKeys() {
        assertThat(unit.read(map("revision", "1", "cursor", "2")), is(emptyMap()));
    }

    @Test
    public void shouldWrite() {
        assertThat(unit.write(map("after", text("2017-07-12T21:24:47Z"), "versions", new ArrayNode(instance).add(1).add(2))),
                is(map("after", "2017-07-12T21:24:47Z", "versions", "[1,2]")));
    }

    private <K, V> Map<K, V> map(final K key, @Nullable final V value) {
        return singletonMap(key, value);
    }

    public <K, V> Map<K, V> map(final K k1, final V v1, final K k2, final V v2) {
        return ImmutableMap.of(k1, v1, k2, v2);
    }

    private TextNode text(final String text) {
        return new TextNode(text);
    }

}
