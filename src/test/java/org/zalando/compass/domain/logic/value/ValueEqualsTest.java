package org.zalando.compass.domain.logic.value;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionLock;
import org.zalando.compass.domain.model.Key;
import org.zalando.compass.domain.model.KeyLock;
import org.zalando.compass.domain.model.Value;
import org.zalando.compass.domain.model.ValueLock;
import org.zalando.compass.domain.model.ValuesLock;

import java.util.Arrays;

@RunWith(Parameterized.class)
public final class ValueEqualsTest<T> {

    private final Class<T> type;

    public ValueEqualsTest(final Class<T> type) {
        this.type = type;
    }

    @Parameterized.Parameters(name= "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {RichDimension.class},
                {RichValue.class},
        });
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(type)
                .withPrefabValues(JsonNode.class, new TextNode("foo"), new TextNode("bar"))
                .verify();
    }

}