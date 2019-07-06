package org.zalando.compass.library;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.BooleanNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasToString;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.library.JsonMutator.withAt;

public class JsonMutatorTest {

    @Test
    public void shouldCreateObject() {
        final var created = withAt(null, "/a", new TextNode(""));

        assertThat(created, hasToString("{\"a\":\"\"}"));
    }

    @Test
    public void shouldCreateArray() {
        final var created = withAt(null, "/0", new TextNode(""));

        assertThat(created, hasToString("[\"\"]"));
    }

    @Test
    public void shouldCreateTextNode() {
        final var created = withAt("", new TextNode("..."));

        assertThat(created, hasToString("\"...\""));
    }

    @Test
    public void shouldSetRootLevelProperty() {
        final JsonNode original = new ObjectNode(instance);
        final var modified = withAt(original, "/a", new TextNode(""));

        assertThat(original, hasToString("{}"));
        assertThat(modified, hasToString("{\"a\":\"\"}"));
    }

    @Test
    public void shouldSetRootLevelElement() {
        final JsonNode node = new ArrayNode(instance);
        final var modified = withAt(node, "/0", new TextNode(""));

        assertThat(node, hasToString("[]"));
        assertThat(modified, hasToString("[\"\"]"));
    }

    @Test
    public void shouldReplaceRootLevelProperty() {
        final JsonNode original = new ObjectNode(instance);
        final var modified = withAt(original, "", BooleanNode.TRUE);

        assertThat(original, hasToString("{}"));
        assertThat(modified, hasToString("true"));
    }

    @Test
    public void shouldReplaceRootLevelElement() {
        final JsonNode original = new ArrayNode(instance);
        final var modified = withAt(original, "", BooleanNode.TRUE);

        assertThat(original, hasToString("[]"));
        assertThat(modified, hasToString("true"));
    }

    @Test
    public void shouldReplaceRootLevelWithImmutableCopy() {
        final JsonNode root = new ObjectNode(instance);
        final var original = new ArrayNode(instance);

        final var modified = withAt(root, "", original);

        assertThat(modified, equalTo(original));
        assertThat(modified, not(sameInstance(original)));
    }

    @Test
    public void shouldCreateDeepProperty() {
        final JsonNode original = new ObjectNode(instance);
        final var modified = withAt(original, "/a/b/c", new TextNode(""));

        assertThat(original, hasToString("{}"));
        assertThat(modified, hasToString("{\"a\":{\"b\":{\"c\":\"\"}}}"));
    }

    @Test
    public void shouldCreateDeepElement() {
        final JsonNode original = new ArrayNode(instance);
        final var modified = withAt(original, "/0/0/0", new TextNode(""));

        assertThat(original, hasToString("[]"));
        assertThat(modified, hasToString("[[[\"\"]]]"));
    }

    @Test
    public void shouldCreateDeepElementAtArbitraryIndices() {
        final JsonNode original = new ArrayNode(instance);
        final var modified = withAt(original, "/0/1/2", new TextNode(""));

        assertThat(original, hasToString("[]"));
        assertThat(modified, hasToString("[[null,[null,null,\"\"]]]"));
    }

    @Test
    public void shouldCreateNestedPropertiesAndElements() {
        final JsonNode original = new ObjectNode(instance);
        final var modified = withAt(original, "/a/b/0", new TextNode(""));

        assertThat(original, hasToString("{}"));
        assertThat(modified, hasToString("{\"a\":{\"b\":[\"\"]}}"));
    }

    @Test
    public void shouldUpdateProperty() {
        final var original = new ObjectNode(instance);
        original.putObject("a").putObject("b").put("c", "...");
        final var modified = withAt(original, "/a/b/c", new TextNode(""));

        assertThat(original, hasToString("{\"a\":{\"b\":{\"c\":\"...\"}}}"));
        assertThat(modified, hasToString("{\"a\":{\"b\":{\"c\":\"\"}}}"));
    }

    @Test
    public void shouldUpdateElement() {
        final var original = new ArrayNode(instance);
        original.addArray().addArray().add("...");
        final var modified = withAt(original, "/0/0/0", new TextNode(""));

        assertThat(original, hasToString("[[[\"...\"]]]"));
        assertThat(modified, hasToString("[[[\"\"]]]"));
    }

}
