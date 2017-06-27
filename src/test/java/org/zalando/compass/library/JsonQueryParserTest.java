package org.zalando.compass.library;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.zalando.compass.resource.JsonQueryParser;

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

public final class JsonQueryParserTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none();

    private final JsonQueryParser unit = new JsonQueryParser(jacksonObjectMapper());

    @Test
    public void shouldParseEmpty() {
        assertThat(unit.parse(emptyMap()), is(emptyMap()));
    }

    @Test
    public void shouldParseStrings() {
        assertThat(unit.parse(map("after", "2017-06-08T15:32:00Z")), is(map("after", text("2017-06-08T15:32:00Z"))));
    }

    @Test
    public void shouldParseQuotedStrings() {
        assertThat(unit.parse(map("test", "\"true\"")), is(map("test", text("true"))));
    }

    @Test
    public void shouldParseStringsWithColon() {
        assertThat(unit.parse(map("test", "1:0")), is(map("test", text("1:0"))));
    }

    @Test
    public void shouldParseIntegerNumbers() {
        assertThat(unit.parse(map("quantity", "123")), is(map("quantity", new BigIntegerNode(new BigInteger("123")))));
    }

    @Test
    public void shouldParseDecimalNumbers() {
        assertThat(unit.parse(map("rate", "0.1")), is(map("rate", new DecimalNode(new BigDecimal("0.1")))));
    }

    @Test
    public void shouldParseBooleans() {
        assertThat(unit.parse(map("active", "true")), is(map("active", BooleanNode.TRUE)));
    }

    @Test
    public void shouldParseObjects() {
        assertThat(unit.parse(map("price", "{\"amount\":25.0}")), is(map("price", new ObjectNode(instance,
                map("amount", new DecimalNode(new BigDecimal(25.0)))))));
    }

    @Test
    public void shouldParseArrays() {
        assertThat(unit.parse(map("users", "[\"alice\",\"bob\"]")), is(map("users", new ArrayNode(instance,
                Arrays.asList(new TextNode("alice"), new TextNode("bob"))))));
    }

    @Test
    public void shouldParseEmptyStringsAsMissing() {
        assertThat(unit.parse(map("country", "")), is(map("country", MissingNode.getInstance())));
    }

    @Test
    public void shouldParseBlankStringsAsMissing() {
        assertThat(unit.parse(map("country", " ")), is(map("country", MissingNode.getInstance())));
    }

    @Test
    public void shouldParseAbsentAsMissing() {
        assertThat(unit.parse(map("country", null)), is(map("country", MissingNode.getInstance())));
    }

    @Test
    public void shouldParseNulls() {
        assertThat(unit.parse(map("country", "null")), is(map("country", NullNode.getInstance())));
    }

    @Test
    public void shouldThrowOriginalExceptionIfFallbackToStringDidntWork() {
        exception.expect(JsonParseException.class);
        exception.expectMessage("Unrecognized token 'A'");

        unit.parse(map("test", "A\tB"));
    }

    private <K, V> Map<K, V> map(final K key, @Nullable final V value) {
        return singletonMap(key, value);
    }

    private TextNode text(final String text) {
        return new TextNode(text);
    }

}