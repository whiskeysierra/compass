package org.zalando.compass.domain.logic;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.RelationRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.io.IOException;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static org.mockito.Mockito.mock;

public class DimensionServiceTest {

    private final SchemaValidator validator = mock(SchemaValidator.class);
    private final RelationRepository relationRepository = new RelationRepository();
    private final DimensionRepository dimensionRepository = mock(DimensionRepository.class);
    private final ValueRepository valueRepository = mock(ValueRepository.class);

    private final DimensionService unit = new DimensionService(validator, relationRepository, dimensionRepository,
            valueRepository);

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnUnknownRelation() throws IOException {
        unit.createOrUpdate(new Dimension("foo", new ObjectNode(instance), "does-not-exist", ""));
    }

}