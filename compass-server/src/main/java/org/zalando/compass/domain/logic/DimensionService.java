package org.zalando.compass.domain.logic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.persistence.DimensionRepository;

import javax.annotation.Nullable;
import java.util.List;

import static java.util.Collections.singleton;

@Service
public class DimensionService {

    private final DimensionRepository repository;

    @Autowired
    public DimensionService(final DimensionRepository repository) {
        this.repository = repository;
    }

    @Nullable
    public Dimension getDimension(final String id) {
        final List<Dimension> dimensions = repository.get(singleton(id));
        return dimensions.isEmpty() ? null : dimensions.get(0);
    }

}
