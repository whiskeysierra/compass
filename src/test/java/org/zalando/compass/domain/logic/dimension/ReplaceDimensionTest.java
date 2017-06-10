package org.zalando.compass.domain.logic.dimension;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Test;
import org.zalando.compass.domain.logic.LockService;
import org.zalando.compass.domain.logic.RelationService;
import org.zalando.compass.domain.logic.ValidationService;
import org.zalando.compass.domain.logic.ValueService;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.persistence.DimensionRepository;
import org.zalando.compass.domain.persistence.NotFoundException;

import java.io.IOException;

import static com.fasterxml.jackson.databind.node.JsonNodeFactory.instance;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class ReplaceDimensionTest {

    private final ValidationService validator = mock(ValidationService.class);
    private final RelationService relationService = mock(RelationService.class);
    private final DimensionRepository dimensionRepository = mock(DimensionRepository.class);
    private final ValueService valueService = mock(ValueService.class);
    private final LockService lockService = mock(LockService.class);

    private final ReplaceDimension unit = new ReplaceDimension(validator, dimensionRepository, relationService,
            valueService, lockService);

    @Test(expected = IllegalArgumentException.class)
    public void shouldFailOnUnknownRelation() throws IOException {
        when(relationService.read(any())).thenThrow(NotFoundException.class);
        unit.replace(new Dimension("foo", new ObjectNode(instance), "does-not-exist", ""));
    }

}