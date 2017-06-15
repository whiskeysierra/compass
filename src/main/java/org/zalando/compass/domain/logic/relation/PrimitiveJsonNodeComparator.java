package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Booleans;

import javax.annotation.Nullable;
import java.util.List;

// TODO library?
final class PrimitiveJsonNodeComparator extends Ordering<JsonNode> {

    @Override
    public int compare(@Nullable final JsonNode left, @Nullable final JsonNode right) {
        if (isNull(left) && isNull(right)) {
            return 0;
        } else if (isNull(left)) {
            return -1;
        } else if (isNull(right)) {
            return 1;
        }

        return compareNonNull(left, right);
    }

    private int compareNonNull(final JsonNode left, final JsonNode right) {
        if (left.getNodeType() == right.getNodeType()) {
            switch (left.getNodeType()) {
                case ARRAY:
                    return lexicographical().compare(left, right);
                case BOOLEAN:
                    return Booleans.compare(left.booleanValue(), right.booleanValue());
                case NUMBER:
                    return left.decimalValue().compareTo(right.decimalValue());
                case OBJECT:
                    final List<String> leftNames = ImmutableList.sortedCopyOf(left::fieldNames);
                    final List<String> rightNames = ImmutableList.sortedCopyOf(right::fieldNames);

                    final ComparisonChain comparison = ComparisonChain.start()
                            .compare(leftNames, rightNames, natural().lexicographical());

                    return leftNames.stream()
                            .reduce(comparison,
                                    (chain, field) -> chain.compare(left.get(field), right.get(field), this),
                                    (a, b) -> {
                                        // TODO combine two chains
                                        throw new UnsupportedOperationException();
                                    })
                            .result();
                case STRING:
                    return left.asText().compareTo(right.asText());
            }
        }

        return left.getNodeType().compareTo(right.getNodeType());
    }

    private static boolean isNull(@Nullable final JsonNode node) {
        return node == null || node.isNull();
    }

}
