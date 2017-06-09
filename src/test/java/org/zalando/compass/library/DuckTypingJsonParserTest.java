package org.zalando.compass.library;

import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.library.JsonConfiguration.yamlObjectMapper;

public final class DuckTypingJsonParserTest {

    private final DuckTypingJsonParser unit = new DuckTypingJsonParser(yamlObjectMapper());

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

    private <K, V> Map<K, V> map(final K key, final V value) {
        return singletonMap(key, value);
    }

    private TextNode text(final String text) {
        return new TextNode(text);
    }

}