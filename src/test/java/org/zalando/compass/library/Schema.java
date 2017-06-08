package org.zalando.compass.library;

import com.fasterxml.jackson.databind.node.ObjectNode;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;

public final class Schema {

    public static ObjectNode stringSchema() {
        return schema("string");
    }

    public static ObjectNode schema(final String type) {
        return new ObjectNode(instance).put("type", type);
    }

}
