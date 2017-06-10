package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;

import java.util.List;
import java.util.Set;

@Service
class DefaultDimensionService implements DimensionService {

    private final ReplaceDimension replace;
    private final ReadDimension readOne;
    private final ReadDimensions readMany;
    private final ReadAllDimensions readAll;
    private final DeleteDimension delete;

    // TODO break cyclic dependency with DefaultValueService
    @Autowired
    DefaultDimensionService(@Lazy final ReplaceDimension replace, final ReadDimension readOne,
            final ReadDimensions readMany, final ReadAllDimensions readAll, final DeleteDimension delete) {
        this.replace = replace;
        this.delete = delete;
        this.readOne = readOne;
        this.readMany = readMany;
        this.readAll = readAll;
    }

    @Override
    public boolean replace(final Dimension dimension) {
        return replace.replace(dimension);
    }

    @Override
    public Dimension read(final String id) {
        return readOne.read(id);
    }

    @Override
    public List<Dimension> readAll(final Set<String> ids) {
        return readMany.read(ids);
    }

    @Override
    public List<Dimension> readAll() {
        return readAll.read();
    }

    @Override
    public void delete(final String id) {
        delete.delete(id);
    }

}
