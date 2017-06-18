package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import org.hamcrest.Matcher;
import org.junit.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.cartesianProduct;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;

public final class PrimitiveJsonNodeComparatorTest {

    private final Comparator<JsonNode> unit = new PrimitiveJsonNodeComparator();

    @Test
    public void shouldCompareNulls() {
        test(nullNode(), nullNode(), equal());
    }

    // TODO array

    @Test
    public void shouldCompareBoolean() {
        test(falseNode(), trueNode(), less());
        test(trueNode(), trueNode(), equal());
        test(trueNode(), falseNode(), greater());
        test(trueNode(), nullNode(), greater());
        test(nullNode(), trueNode(), less());
    }

    @Test
    public void shouldCompareNumbers() {
        testNumbers(0, 1, less());
        testNumbers(0, 0, equal());
        testNumbers(1, 0, greater());
    }

    private void testNumbers(final int left, final int right, final Matcher<Integer> matcher) {
        for (final List<JsonNode> nodes : cartesianProduct(numbers(left), numbers(right))) {
            test(nodes.get(0), nodes.get(1), matcher);
        }
    }

    private List<JsonNode> numbers(final int value) {
        return ImmutableList.of(
                new ShortNode((short) value),
                new IntNode(value),
                new LongNode(value),
                new BigIntegerNode(BigInteger.valueOf(value)),
                new FloatNode(value),
                new DoubleNode(value),
                new DecimalNode(BigDecimal.valueOf(value)));
    }

    // TODO objects

    @Test
    public void shouldCompareStrings() {
        test(textNode("a"), textNode("b"), less());
        test(textNode("a"), textNode("a"), equal());
        test(textNode("b"), textNode("a"), greater());
        test(textNode("a"), nullNode(), greater());
        test(nullNode(), textNode("a"), less());
    }

    // TODO type mismatch
    // TODO unsupported types

    private Matcher<Integer> less() {
        return lessThan(0);
    }

    private Matcher<Integer> equal() {
        return equalTo(0);
    }

    private Matcher<Integer> greater() {
        return greaterThan(0);
    }

    private JsonNode textNode(final String text) {
        return new TextNode(text);
    }

    private JsonNode trueNode() {
        return BooleanNode.TRUE;
    }

    private JsonNode falseNode() {
        return BooleanNode.FALSE;
    }

    private JsonNode nullNode() {
        return NullNode.getInstance();
    }

    private void test(final JsonNode left, final JsonNode right, final Matcher<Integer> matcher) {
        assertThat(unit.compare(left, right), matcher);
    }

}