package org.zalando.compass.library;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static java.util.Collections.nCopies;

public final class JsonMutator {

    public static void setAt(final JsonNode node, final String pointer, final JsonNode value) {
        setAt(node, JsonPointer.compile(pointer), value);
    }

    public static void setAt(final JsonNode node, final JsonPointer pointer, final JsonNode value) {
        if (value.isMissingNode()) {
            return;
        }

        final JsonNode parent = createAncestors(node, pointer);
        set(parent, pointer.last(), value);
    }

    private static JsonNode createAncestors(final JsonNode node, final JsonPointer pointer) {
        final JsonPointer head = pointer.head();

        final boolean parentIsAbsent = node.at(head).isMissingNode();

        if (parentIsAbsent) {
            final JsonNode grandParent = createAncestors(node, head);
            final JsonPointer me = pointer.last();
            final JsonNode parent = me.mayMatchElement() ? new ArrayNode(instance) : new ObjectNode(instance);

            set(grandParent, head.last(), parent);
        }

        return node.at(head);
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
