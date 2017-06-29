package org.zalando.compass.domain.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;

@RunWith(Parameterized.class)
public final class DomainModelEqualsTest<T> {

    private final Class<T> type;

    public DomainModelEqualsTest(final Class<T> type) {
        this.type = type;
    }

    @Parameterized.Parameters(name= "{0}")
    public static Iterable<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {Dimension.class},
                {DimensionRevision.class},
                {Key.class},
                {KeyRevision.class},
                {Value.class},
                {Revision.class},
                {Page.class},
        });
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(type)
                .withPrefabValues(JsonNode.class, new TextNode("foo"), new TextNode("bar"))
                .verify();
    }

}