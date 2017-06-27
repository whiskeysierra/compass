package org.zalando.compass.library;

import com.fasterxml.jackson.core.JsonPointer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

public final class JsonMutator {

    public static void setAt(final JsonNode node, final String pointer, final JsonNode value) {
        setAt(node, JsonPointer.compile(pointer), value);
    }

    public static void setAt(final JsonNode node, final JsonPointer pointer, final JsonNode value) {
        if (value.isMissingNode()) {
            return;
        }

        final JsonNode parent = createParents(node, pointer);
        set(parent, pointer.last(), value);
    }

    private static JsonNode createParents(final JsonNode node, final JsonPointer pointer) {
        final JsonPointer head = pointer.head();

        if (node.at(head).isMissingNode()) {
            createParents(node, head);
            set(node, head, pointer.mayMatchProperty() ? new ObjectNode(instance) : new ArrayNode(instance));
        }

        return node.at(head);
    }

    private static void set(final JsonNode node, final JsonPointer pointer, final JsonNode value) {
        if (pointer.mayMatchProperty()) {
            ObjectNode.class.cast(node).set(pointer.getMatchingProperty(), value);
        } else if (pointer.mayMatchElement()) {
            ArrayNode.class.cast(node).set(pointer.getMatchingIndex(), value);
        }
    }

}