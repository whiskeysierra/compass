package org.zalando.compass.domain.model.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BigIntegerNode;
import com.fasterxml.jackson.databind.node.BinaryNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.DecimalNode;
import com.fasterxml.jackson.databind.node.DoubleNode;
import com.fasterxml.jackson.databind.node.FloatNode;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.LongNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.NullNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.POJONode;
import com.fasterxml.jackson.databind.node.ShortNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.hamcrest.Matcher;
import org.junit.Test;

import javax.annotation.Nullable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static com.google.common.collect.Lists.cartesianProduct;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.domain.model.relation.NaturalOrderJsonComparator.throwingCombiner;

public final class NaturalOrderJsonComparatorTest {

    private final Comparator<JsonNode> unit = NaturalOrderJsonComparator.comparingJson();

    @Test
    public void shouldCompareNulls() {
        test(null, nullNode(), less());
        test(nullNode(), nullNode(), equal());
        test(nullNode(), null, greater());
        test(null, null, equal());
    }

    @Test
    public void shouldCompareArrays() {
        test(arrayNode(), arrayNode(), equal());
        test(arrayNode(), arrayNode(textNode("A")), less());
        test(arrayNode(textNode("A")), arrayNode(textNode("B")), less());
        test(arrayNode(textNode("B")), arrayNode(textNode("B")), equal());
        test(arrayNode(textNode("A")), arrayNode(textNode("A"), textNode("B")), less());
        test(arrayNode(textNode("C")), arrayNode(textNode("A"), textNode("B")), greater());
    }

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
                intNode(value),
                new LongNode(value),
                new BigIntegerNode(BigInteger.valueOf(value)),
                new FloatNode(value),
                new DoubleNode(value),
                new DecimalNode(BigDecimal.valueOf(value)));
    }

    @Test
    public void shouldCompareObjects() {
        test(objectNode(), objectNode(), equal());
        test(objectNode(), objectNode("name", textNode("alice")), less());
        test(objectNode("name", textNode("alice")), objectNode("name", textNode("alice")), equal());
        test(objectNode("name", textNode("alice")), objectNode("name", textNode("bob")), less());
        test(objectNode("name", textNode("charlie")), objectNode("name", textNode("bob")), greater());
        test(objectNode("age", intNode(17)), objectNode("name", textNode("alice"), "age", intNode(17)), less());
        test(objectNode("name", textNode("alice")), objectNode("name", textNode("alice"), "age", intNode(17)), greater());
        test(objectNode("name", textNode("alice"), "age", intNode(18)),
                objectNode("name", textNode("bob"), "age", intNode(17)), greater());
        test(objectNode("name", textNode("alice"), "age", intNode(18)),
                objectNode("age", intNode(17), "name", textNode("bob")), greater());
    }

    @Test
    public void shouldCompareStrings() {
        test(textNode("a"), textNode("b"), less());
        test(textNode("a"), textNode("a"), equal());
        test(textNode("b"), textNode("a"), greater());
        test(textNode("a"), nullNode(), greater());
        test(nullNode(), textNode("a"), less());
    }

    @Test
    public void shouldCompareMismatchingTypes() {
        test(arrayNode(), trueNode(), less());
        test(trueNode(), intNode(17), less());
        test(intNode(17), objectNode(), less());
        test(objectNode(), textNode(""), less());
        test(textNode(""), arrayNode(), greater());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailOnBinary() {
        unit.compare(new BinaryNode(new byte[0]), textNode(""));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailOnMissing() {
        unit.compare(MissingNode.getInstance(), textNode(""));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldFailOnPojo() {
        unit.compare(new POJONode(new Object()), textNode(""));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldCoverThrowingMerger() {
        throwingCombiner(nullNode(), nullNode());
    }

    private Matcher<Integer> less() {
        return lessThan(0);
    }

    private Matcher<Integer> equal() {
        return equalTo(0);
    }

    private Matcher<Integer> greater() {
        return greaterThan(0);
    }

    private JsonNode arrayNode(final JsonNode... elements) {
        return new ArrayNode(instance, Arrays.asList(elements));
    }

    private JsonNode trueNode() {
        return BooleanNode.TRUE;
    }

    private JsonNode falseNode() {
        return BooleanNode.FALSE;
    }

    private JsonNode intNode(final int value) {
        return new IntNode(value);
    }

    private JsonNode objectNode() {
        return new ObjectNode(instance);
    }

    private JsonNode objectNode(final String key, final JsonNode value) {
        return new ObjectNode(instance, Collections.singletonMap(key, value));
    }

    private JsonNode objectNode(final String key1, final JsonNode value1, final String key2, final JsonNode value2) {
        return new ObjectNode(instance, ImmutableMap.of(key1, value1, key2, value2));
    }

    private JsonNode textNode(final String text) {
        return new TextNode(text);
    }

    private JsonNode nullNode() {
        return NullNode.getInstance();
    }

    private void test(@Nullable final JsonNode left, @Nullable final JsonNode right, final Matcher<Integer> matcher) {
        assertThat(unit.compare(left, right), matcher);
    }

}
