package org.zalando.compass;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.zalando.compass.kernel.domain.model.Dimension;
import org.zalando.compass.revision.domain.model.DimensionRevision;
import org.zalando.compass.kernel.domain.model.Key;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.kernel.domain.model.Revision;
import org.zalando.compass.kernel.domain.model.Value;
import org.zalando.compass.revision.domain.model.ValueRevision;

import java.util.Arrays;

@RunWith(Parameterized.class)
public final class EqualsTest<T> {

    private final Class<T> type;

    public EqualsTest(final Class<T> type) {
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
                {ValueRevision.class},
                {Revision.class}
        });
    }

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(type)
                .withPrefabValues(JsonNode.class, new TextNode("foo"), new TextNode("bar"))
                .verify();
    }

}
