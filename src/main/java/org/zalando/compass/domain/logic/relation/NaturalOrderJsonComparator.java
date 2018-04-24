package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ComparisonChain;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Comparators.lexicographical;
import static com.google.common.collect.Lists.newArrayList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;

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
                return this::compareFieldsRecursively;
            default: // should've been "case STRING" but this is easier to cover
                return comparing(JsonNode::asText);
        }
    }

    private int compareFieldsRecursively(final JsonNode left, final JsonNode right) {
        final Collection<String> leftFieldNames = sortedFieldNames(left);
        final Collection<String> rightFieldNames = sortedFieldNames(right);

        final ComparisonChain start = ComparisonChain.start()
                .compare(leftFieldNames, rightFieldNames, lexicographical(String::compareTo));

        return leftFieldNames.stream()
                .reduce(start, (chain, name) ->
                        chain.compare(left.get(name), right.get(name), this), NaturalOrderJsonComparator::throwingCombiner)
                .result();
    }

    private Collection<String> sortedFieldNames(final JsonNode node) {
        final List<String> fieldNames = newArrayList(node.fieldNames());
        fieldNames.sort(naturalOrder());
        return fieldNames;
    }

    @VisibleForTesting
    @SuppressWarnings("unused")
    static <T> T throwingCombiner(final T left, final T right) {
        throw new UnsupportedOperationException();
    }

    public static Comparator<JsonNode> comparingJson() {
        return nullsFirst(new NaturalOrderJsonComparator());
    }

}
