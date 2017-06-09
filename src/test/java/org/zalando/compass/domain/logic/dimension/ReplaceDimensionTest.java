package org.zalando.compass.domain.logic.dimension;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.RelationRepository;
import org.zalando.compass.domain.persistence.ValueRepository;

import java.io.IOException;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static org.mockito.Mockito.mock;

public final class ReplaceDimensionTest {

    private final ValidationService validator = mock(ValidationService.class);
    private final RelationRepository relationRepository = new RelationRepository();
    private final DimensionRepository dimensionRepository = mock(DimensionRepository.class);
    private final ValueRepository valueRepository = mock(ValueRepository.class);


    private final ReplaceDimension unit = new ReplaceDimension(validator, dimensionRepository, relationRepository,
            valueRepository);

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnUnknownRelation() throws IOException {
        unit.replace(new Dimension("foo", new ObjectNode(instance), "does-not-exist", ""));
    }

}