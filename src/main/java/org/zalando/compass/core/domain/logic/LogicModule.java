package org.zalando.compass.core.domain.logic;

import org.zalando.compass.core.domain.api.DimensionService;
import org.zalando.compass.core.domain.spi.event.EventPublisher;
import org.zalando.compass.core.domain.spi.repository.DimensionRepository;
import org.zalando.compass.core.domain.spi.repository.lock.DimensionLockRepository;
import org.zalando.compass.core.domain.spi.repository.lock.ValueLockRepository;
import org.zalando.compass.core.domain.spi.validation.ValidationService;
import org.zalando.compass.revision.domain.api.DimensionRevisionService;

final class LogicModule {

    public DimensionService dimensionService(
            final ValidationService validator,
            final DimensionRevisionService dimensionRevisionService,
            final EventPublisher publisher) {

        final InMemoryDimensionRepository repository = new InMemoryDimensionRepository();

        return dimensionService(
                validator,
                repository, // data
                repository, // locking
                new InMemoryValueRepository(),
                dimensionRevisionService,
                publisher);
    }

    public DimensionService dimensionService(
            final ValidationService validator,
            final DimensionRepository repository,
            final DimensionLockRepository dimensionLockRepository,
            final ValueLockRepository valueLockRepository,
            final DimensionRevisionService dimensionRevisionService,
            final EventPublisher publisher) {

        final DimensionLocking locking = new DimensionLocking(dimensionLockRepository, valueLockRepository);

        final ReplaceDimension replace = new ReplaceDimension(
                locking,
                validator,
                repository,
                publisher);

        final ReadDimension read = new ReadDimension(
                repository,
                dimensionRevisionService
        );

        final DeleteDimension delete = new DeleteDimension(
                locking,
                repository,
                publisher
        );

        return new DefaultDimensionService(replace, read, delete);
    }

}
