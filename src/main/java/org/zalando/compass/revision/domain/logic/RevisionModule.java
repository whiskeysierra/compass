package org.zalando.compass.revision.domain.logic;

import org.zalando.compass.revision.domain.api.DimensionRevisionService;
import org.zalando.compass.core.domain.spi.repository.RevisionRepository;
import org.zalando.compass.revision.domain.spi.repository.DimensionRevisionRepository;

public final class RevisionModule {

    public DimensionRevisionService dimensionRevisionService(
            final DimensionRevisionRepository dimensionRevisionRepository,
            final RevisionRepository revisionRepository) {
        return new DefaultDimensionRevisionService(
                new ReadDimensionRevision(dimensionRevisionRepository, revisionRepository));
    }

}
