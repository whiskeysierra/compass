package org.zalando.compass.core.domain.logic;

import org.springframework.context.ApplicationEventPublisher;
import org.zalando.compass.revision.domain.api.DimensionRevisionService;
import org.zalando.compass.core.domain.api.DimensionService;
import org.zalando.compass.core.domain.spi.repository.DimensionRepository;
import org.zalando.compass.core.domain.spi.repository.RevisionRepository;
import org.zalando.compass.core.domain.spi.repository.lock.DimensionLockRepository;
import org.zalando.compass.core.domain.spi.repository.lock.ValueLockRepository;
import org.zalando.compass.core.domain.spi.validation.ValidationService;
import org.zalando.compass.core.infrastructure.relations.ServiceLoaderRelationRepository;

import java.time.Clock;

final class LogicModule {

    DimensionService dimensionService(
            final ValidationService validator,
            final DimensionRevisionService dimensionRevisionService,
            final ApplicationEventPublisher publisher) {

        final InMemoryDimensionRepository repository = new InMemoryDimensionRepository();

        return dimensionService(
                validator,
                repository, // data
                repository, // locking
                new InMemoryValueRepository(),
                new InMemoryRevisionRepository(),
                dimensionRevisionService,
                publisher);
    }

    DimensionService dimensionService(
            final ValidationService validator,
            final DimensionRepository repository,
            final DimensionLockRepository dimensionLockRepository,
            final ValueLockRepository valueLockRepository,
            final RevisionRepository revisionRepository,
            final DimensionRevisionService dimensionRevisionService,
            final ApplicationEventPublisher publisher) {

        final DimensionLocking locking = new DimensionLocking(dimensionLockRepository, valueLockRepository);

        final RevisionService revisionService = new RevisionService(
                Clock.systemUTC(), revisionRepository);

        final ReplaceDimension replace = new ReplaceDimension(
                locking,
                new DefaultRelationService(new ServiceLoaderRelationRepository()),
                validator,
                repository,
                revisionService,
                publisher);

        final ReadDimension read = new ReadDimension(
                repository,
                dimensionRevisionService
        );

        final DeleteDimension delete = new DeleteDimension(
                locking,
                repository,
                revisionService,
                publisher
        );

        return new DefaultDimensionService(replace, read, delete);
    }

}
