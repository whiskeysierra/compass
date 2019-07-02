package org.zalando.compass.revision.domain.logic;

import org.zalando.compass.revision.domain.api.RevisionService;
import org.zalando.compass.revision.domain.api.DimensionRevisionService;
import org.zalando.compass.revision.domain.spi.repository.DimensionRevisionRepository;

public final class RevisionModule {

    public DimensionRevisionService dimensionRevisionService(final RevisionService service) {
        return dimensionRevisionService(new InMemoryDimensionRevisionRepository(), service);
    }

    public DimensionRevisionService dimensionRevisionService(
            final DimensionRevisionRepository repository,
            final RevisionService service) {

        final ReadDimensionRevision read = new ReadDimensionRevision(repository, service);

        return new DefaultDimensionRevisionService(read);
    }

}
