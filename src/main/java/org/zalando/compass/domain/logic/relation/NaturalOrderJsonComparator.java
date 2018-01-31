package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.collect.ComparisonChain;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.collect.Comparators.lexicographical;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.Spliterator.DISTINCT;
import static java.util.Spliterator.IMMUTABLE;
import static java.util.Spliterator.NONNULL;
import static java.util.Spliterator.SIZED;
import static java.util.Spliterators.spliterator;
import static java.util.stream.StreamSupport.stream;

final class NaturalOrderJsonComparator implements Comparator<JsonNode> {

    private NaturalOrderJsonComparator() {

    }

    @Override
    public int compare(@Nonnull final JsonNode left, @Nonnull final JsonNode right) {
        checkSupportedType(left);
        checkSupportedType(right);

        return ComparisonChain.start()
                .compare(left, right, comparing(JsonNode::isNull).reversed())
                .compare(left, right, comparing(JsonNode::getNodeType)) // TODO specify
                .compare(left, right, selectComparator(left.getNodeType()))
                .result();
    }

    private void checkSupportedType(final JsonNode node) {
        final JsonNodeType type = node.getNodeType();

        switch (type) {
            case BINARY:
            case MISSING:
            case POJO:
                throw new UnsupportedOperationException("Unsupported node type:" + type);
        }
    }

    private Comparator<? super JsonNode> selectComparator(final JsonNodeType type) {
        switch (type) {
            case ARRAY:
                return lexicographical(this);
            case BOOLEAN:
                return comparing(JsonNode::booleanValue);
            case NUMBER:
                return comparing(JsonNode::decimalValue);
            case OBJECT:
                return comparing(this::sortedFieldNames, lexicographical(String::compareTo))
                        .thenComparing(this::compareFieldsRecursively);
            default: // should've been "case STRING" but this is easier to cover
                return comparing(JsonNode::asText);
        }
    }

    private int compareFieldsRecursively(final JsonNode left, final JsonNode right) {
        return streamFields(left)
                .sorted()
                .mapToInt(name -> compare(left.get(name), right.get(name)))
                .filter(result -> result != 0)
                .findFirst().orElse(0);
    }

    private Stream<String> streamFields(final JsonNode node) {
        return stream(spliterator(node.fieldNames(), node.size(), DISTINCT | IMMUTABLE | NONNULL | SIZED), false);
    }

    private Iterable<String> sortedFieldNames(final JsonNode node) {
        final List<String> fieldNames = newArrayList(node.fieldNames());
        fieldNames.sort(naturalOrder());
        return fieldNames;
    }

    public static Comparator<JsonNode> comparingJson() {
        return nullsFirst(new NaturalOrderJsonComparator());
    }

}
