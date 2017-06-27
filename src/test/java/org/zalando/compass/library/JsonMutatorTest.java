package org.zalando.compass.library;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import org.junit.Test;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static org.hamcrest.Matchers.hasToString;
import static org.junit.Assert.assertThat;
import static org.zalando.compass.library.JsonMutator.setAt;

public class JsonMutatorTest {

    @Test
    public void shouldSetRootLevelProperty() {

    }

    @Test
    public void shouldSetRootLevelElement() {

    }

    @Test
    public void shouldCreateDeepProperty() {
        final ObjectNode node = new ObjectNode(instance);
        setAt(node, "/a/b/c", new TextNode(""));

        assertThat(node, hasToString("{\"a\":{\"b\":{\"c\":\"\"}}}"));
    }

    @Test
    public void shouldCreateDeepElement() {
        final ArrayNode node = new ArrayNode(instance);
        setAt(node, "/0/0/0", new TextNode(""));

        assertThat(node, hasToString("[[[\"\"]]]"));
    }

    @Test
    public void shouldCreateDeepElementAtArbitraryIndices() {
        final ArrayNode node = new ArrayNode(instance);
        setAt(node, "/0/1/2", new TextNode(""));

        assertThat(node, hasToString("[[null,[null,null,\"\"]]]"));
    }

    @Test
    public void shouldCreateNestedPropertiesAndElements() {
        final ObjectNode node = new ObjectNode(instance);
        setAt(node, "/a/b/0", new TextNode(""));

        assertThat(node, hasToString("{\"a\":{\"b\":[\"\"]}}"));
    }

    @Test
    public void shouldUpdateProperty() {
        final ObjectNode node = new ObjectNode(instance);
        node.putObject("a").putObject("b").put("c", "...");
        setAt(node, "/a/b/c", new TextNode(""));

        assertThat(node, hasToString("{\"a\":{\"b\":{\"c\":\"\"}}}"));
    }

    @Test
    public void shouldUpdateElement() {
        final ArrayNode node = new ArrayNode(instance);
        node.addArray().addArray().add("...");
        setAt(node, "/0/0/0", new TextNode(""));

        assertThat(node, hasToString("[[[\"\"]]]"));
    }

    // TODO should update

}