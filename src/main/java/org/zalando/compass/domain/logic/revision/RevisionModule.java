package org.zalando.compass.domain.logic.revision;

import org.zalando.compass.domain.api.DimensionRevisionService;
import org.zalando.compass.domain.spi.repository.RevisionRepository;
import org.zalando.compass.domain.spi.repository.revision.DimensionRevisionRepository;

public final class RevisionModule {

    public DimensionRevisionService dimensionRevisionService(
            final DimensionRevisionRepository dimensionRevisionRepository,
            final RevisionRepository revisionRepository) {
        return new DefaultDimensionRevisionService(
                new ReadDimensionRevision(dimensionRevisionRepository, revisionRepository));
    }

}
