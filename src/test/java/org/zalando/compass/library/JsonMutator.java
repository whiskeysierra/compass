package org.zalando.compass.library;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import javax.annotation.CheckReturnValue;
import javax.annotation.Nullable;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static java.util.Collections.nCopies;

public final class JsonMutator {

    @CheckReturnValue
    public static JsonNode withAt(final String pointer, final JsonNode value) {
        return withAt(JsonPointer.compile(pointer), value);
    }

    @CheckReturnValue
    public static JsonNode withAt(final JsonPointer pointer, final JsonNode value) {
        return withAt(createParentOf(pointer), pointer, value);
    }

    @CheckReturnValue
    public static JsonNode withAt(@Nullable final JsonNode node, final String pointer, final JsonNode value) {
        return withAt(node, JsonPointer.compile(pointer), value);
    }

    @CheckReturnValue
    public static JsonNode withAt(@Nullable final JsonNode original, final JsonPointer pointer, final JsonNode value) {
        if (original == null) {
            return withAt(pointer, value);
        }

        final JsonNode node = original.deepCopy();

        if (value.isMissingNode()) {
            return node;
        }

        if (pointer.matches()) {
            return value.deepCopy();
        }

        final JsonNode parent = createAncestors(node, pointer);
        set(parent, pointer.last(), value);
        return node;
    }

    private static JsonNode createAncestors(final JsonNode node, final JsonPointer pointer) {
        final JsonPointer head = pointer.head();

        final boolean parentIsAbsent = node.at(head).isMissingNode();

        if (parentIsAbsent) {
            final JsonNode grandParent = createAncestors(node, head);
            final JsonPointer me = pointer.last();
            final JsonNode parent = createParentOf(me);

            set(grandParent, head.last(), parent);
        }

        return node.at(head);
    }

    private static JsonNode createParentOf(final JsonPointer pointer) {
        return pointer.mayMatchElement() ? new ArrayNode(instance) : new ObjectNode(instance);
    }

    private static void set(final JsonNode node, final JsonPointer pointer, final JsonNode value) {
        if (pointer.mayMatchElement()) {
            final ArrayNode array = ArrayNode.class.cast(node);
            final int index = pointer.getMatchingIndex();
            if (array.has(index)) {
                array.set(index, value);
            } else if (index > array.size()) {
                array.addAll(nCopies(index - array.size(), array.nullNode()));
                array.add(value);
            } else {
                array.insert(index, value);
            }
        } else if (pointer.mayMatchProperty()) {
            ObjectNode.class.cast(node).set(pointer.getMatchingProperty(), value);
        }
    }

}
