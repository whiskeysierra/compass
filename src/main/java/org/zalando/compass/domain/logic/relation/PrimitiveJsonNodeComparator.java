package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Booleans;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

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

    private static boolean isNull(@Nullable final JsonNode node) {
        return node == null || node.isNull();
    }

    private int compareNonNull(final JsonNode left, final JsonNode right) {
        checkSupportedType(left.getNodeType());
        checkSupportedType(right.getNodeType());

        return ComparisonChain.start()
                .compare(left, right, natural().onResultOf(JsonNode::getNodeType))
                .compare(left, right, selectComparator(left.getNodeType()))
                .result();
    }

    private void checkSupportedType(final JsonNodeType type) {
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
                return this::compareArrays;
            case BOOLEAN:
                return this::compareBooleans;
            case NUMBER:
                return this::compareNumbers;
            case OBJECT:
                return this::compareObjects;
            default: // should've been "case STRING" but this is easier to cover
                return this::compareStrings;
        }
    }

    private int compareArrays(final JsonNode left, final JsonNode right) {
        return lexicographical().compare(left, right);
    }

    private int compareBooleans(final JsonNode left, final JsonNode right) {
        return Booleans.compare(left.booleanValue(), right.booleanValue());
    }

    private int compareNumbers(final JsonNode left, final JsonNode right) {
        return left.decimalValue().compareTo(right.decimalValue());
    }

    private int compareObjects(final JsonNode left, final JsonNode right) {
        final List<String> leftNames = sortedFieldNames(left);
        final List<String> rightNames = sortedFieldNames(right);

        {
            final int result = Ordering.<String>natural().lexicographical()
                    .compare(leftNames, rightNames);

            if (result != 0) {
                return result;
            }
        }

        for (final String name : leftNames) {
            final int result = compare(left.get(name), right.get(name));
            if (result != 0) {
                return result;
            }
        }

        return 0;
    }

    private List<String> sortedFieldNames(final JsonNode node) {
        final List<String> fieldNames = newArrayList(node.fieldNames());
        Collections.sort(fieldNames);
        return fieldNames;
    }

    private int compareStrings(final JsonNode left, final JsonNode right) {
        return left.asText().compareTo(right.asText());
    }

}
