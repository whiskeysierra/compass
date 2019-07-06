package org.zalando.compass.revision.domain.logic;

import org.zalando.compass.core.domain.spi.repository.RevisionRepository;
import org.zalando.compass.revision.domain.api.DimensionRevisionService;
import org.zalando.compass.revision.domain.api.KeyRevisionService;
import org.zalando.compass.revision.domain.api.RevisionService;
import org.zalando.compass.revision.domain.api.ValueRevisionService;
import org.zalando.compass.revision.domain.spi.repository.DimensionRevisionRepository;
import org.zalando.compass.revision.domain.spi.repository.KeyRevisionRepository;
import org.zalando.compass.revision.domain.spi.repository.ValueRevisionRepository;

import java.time.Clock;

public final class RevisionModule {

    public RevisionService revisionService(final Clock clock) {
        return revisionService(clock, new InMemoryRevisionRepository());
    }

    public RevisionService revisionService(final Clock clock, final RevisionRepository repository) {
        return new DefaultRevisionService(clock, repository);
    }

    public DimensionRevisionService dimensionRevisionService(final RevisionService service) {
        return dimensionRevisionService(new InMemoryDimensionRevisionRepository(), service);
    }

    public DimensionRevisionService dimensionRevisionService(
            final DimensionRevisionRepository repository,
            final RevisionService service) {

        final var create = new CreateDimensionRevision(repository);
        final var read = new ReadDimensionRevision(repository, service);

        return new DefaultDimensionRevisionService(create, read);
    }

    public KeyRevisionService keyRevisionService(final RevisionService service) {
        return keyRevisionService(new InMemoryKeyRevisionRepository(), service);
    }

    public KeyRevisionService keyRevisionService(
            final KeyRevisionRepository repository,
            final RevisionService service) {

        final var create = new CreateKeyRevision(repository);
        final var read = new ReadKeyRevision(repository, service);

        return new DefaultKeyRevisionService(create, read);
    }

    public ValueRevisionService valueRevisionService(final RevisionService service) {
        return valueRevisionService(new InMemoryValueRevisionRepository(), service);
    }

    public ValueRevisionService valueRevisionService(
            final ValueRevisionRepository repository,
            final RevisionService service) {

        final var create = new CreateValueRevision(repository);
        final var read = new ReadValueRevision(repository, service);

        return new DefaultValueRevisionService(create, read);
    }

}
