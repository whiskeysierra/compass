package org.zalando.compass.core.domain.logic;

import org.junit.Before;
import org.junit.Test;
import org.zalando.compass.core.domain.api.DimensionService;
import org.zalando.compass.core.domain.api.NotFoundException;
import org.zalando.compass.core.domain.model.Dimension;
import org.zalando.compass.core.domain.model.relation.Equality;
import org.zalando.compass.core.domain.spi.event.EventPublisher;
import org.zalando.compass.core.domain.spi.validation.ValidationService;
import org.zalando.compass.library.pagination.Cursor;
import org.zalando.compass.library.pagination.PageResult;
import org.zalando.compass.revision.domain.api.DimensionRevisionService;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class DimensionServiceTest {

    private static final Equality EQ = new Equality();

    private final ValidationService validator = mock(ValidationService.class);
    private final DimensionRevisionService dimensionRevisionService = mock(DimensionRevisionService.class);
    private final EventPublisher publisher = mock(EventPublisher.class);

    private final DimensionService unit = new LogicModule()
            .dimensionService(validator, dimensionRevisionService, publisher);

    @Before
    public void setUp() {
        when(dimensionRevisionService.readRevisions(any(), any())).thenReturn(PageResult.empty());
    }

    @Test
    public void shouldCreate() {
        unit.create(new Dimension("country", null, EQ, "ISO-3166-1 alpha-2"), "Added new country dimension");

        final var revisioned = unit.read("country");
        final var dimension = revisioned.getEntity();

        assertThat(dimension.getRelation(), is(EQ));
    }

    @Test
    public void shouldDelete() {
        final var country = new Dimension("country", null, EQ, "ISO-3166-1 alpha-2");
        unit.create(country, "Added new country dimension");
        unit.delete(country, "Removed country dimension");

        assertThrows(NotFoundException.class, () -> unit.read("country"));
        assertThrows(NotFoundException.class, () -> unit.readOnly("country"));
    }

    @Test
    public void shouldReadPage() {
        final var country = new Dimension("country", null, EQ, "ISO-3166-1 alpha-2");
        final var postalCode = new Dimension("postal-code", null, EQ, "Postal code");

        unit.create(country, "Added new country dimension");
        unit.create(postalCode, "Added new postal code dimension");

        final var result = unit.readPage("", Cursor.<String, Void>initial().with(10).paginate());

        assertThat(result.getElements(), hasSize(2));
        assertThat(result.getHead(), is(country));
        assertThat(result.getTail(), is(postalCode));
    }

    @Test
    public void shouldReadPageWithQuery() {
        final var postalCode = new Dimension("postal-code", null, EQ, "Postal code");
        unit.create(new Dimension("country", null, EQ, "ISO-3166-1 alpha-2"), "Added new country dimension");
        unit.create(postalCode, "Added new postal code dimension");

        final var result = unit.readPage("post", Cursor.<String, Void>initial().with(10).paginate());

        assertThat(result.getElements(), hasSize(1));
        assertThat(result.getHead(), is(postalCode));
        assertThat(result.getTail(), is(postalCode));
    }

}
