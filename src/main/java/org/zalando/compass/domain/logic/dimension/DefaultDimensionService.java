package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;

import javax.annotation.Nullable;
import java.util.List;

@Service
class DefaultDimensionService implements DimensionService {

    private final ReplaceDimension replace;
    private final ReadDimension read;
    private final DeleteDimension delete;

    @Autowired
    DefaultDimensionService(final ReplaceDimension replace, final ReadDimension read, final DeleteDimension delete) {
        this.replace = replace;
        this.delete = delete;
        this.read = read;
    }

    @Transactional
    @Override
    public boolean replace(final Dimension dimension) {
        return replace.replace(dimension);
    }

    @Override
    public Dimension read(final String id) {
        return read.read(id);
    }

    @Override
    public List<Dimension> readAll(@Nullable final String term) {
        return read.readAll(term);
    }

    @Transactional
    @Override
    public void delete(final String id) {
        delete.delete(id);
    }

}
