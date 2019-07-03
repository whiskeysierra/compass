package org.zalando.compass;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;
import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.Key;
import org.zalando.compass.core.domain.model.Revision;
import org.zalando.compass.core.domain.model.Value;
import org.zalando.compass.revision.domain.model.DimensionRevision;
import org.zalando.compass.revision.domain.model.KeyRevision;
import org.zalando.compass.revision.domain.model.ValueRevision;

import java.util.Arrays;

public final class DimensionEqualsTest<T> {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(Dimension.class)
                .withOnlyTheseFields("id")
                .withPrefabValues(JsonNode.class, new TextNode("foo"), new TextNode("bar"))
                .verify();
    }

}
