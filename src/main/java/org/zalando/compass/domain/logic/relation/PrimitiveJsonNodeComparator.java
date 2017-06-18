package org.zalando.compass.domain.logic.relation;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Booleans;

import javax.annotation.Nullable;
import java.util.Collections;
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
        if (left.getNodeType() == right.getNodeType()) {
            switch (left.getNodeType()) {
                case ARRAY:
                    return compareArrays(left, right);
                case BOOLEAN:
                    return compareBooleans(left, right);
                case NUMBER:
                    return compareNumbers(left, right);
                case OBJECT:
                    return compareObjects(left, right);
                case STRING:
                    return compareStrings(left, right);
            }
        }

        throw new UnsupportedOperationException(
                "Unsupported types: " + left.getNodeType() + " and " + right.getNodeType());
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

        final int result = Ordering.<String>natural().lexicographical()
                .compare(leftNames, rightNames);

        if (result != 0) {
            return result;
        }

        for (final String name : leftNames) {
            if (compare(left.get(name), right.get(name)) != 0) {
                return 0;
            }
        }

        return 0;
    }

    private int compareStrings(final JsonNode left, final JsonNode right) {
        return left.asText().compareTo(right.asText());
    }

    private List<String> sortedFieldNames(final JsonNode node) {
        final List<String> fieldNames = newArrayList(node.fieldNames());
        Collections.sort(fieldNames);
        return fieldNames;
    }

}
