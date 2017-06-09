package org.zalando.compass.domain.logic.dimension;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.zalando.compass.domain.logic.DimensionService;
import org.zalando.compass.domain.model.Dimension;

@Service
public class DefaultDimensionService implements DimensionService {

    private final ReplaceDimension replace;
    private final DeleteDimension delete;

    @Autowired
    public DefaultDimensionService(final ReplaceDimension replace, final DeleteDimension delete) {
        this.replace = replace;
        this.delete = delete;
    }

    @Override public boolean replace(final Dimension dimension) {
        return replace.replace(dimension);
    }

    @Override public void delete(final String id) {
        delete.delete(id);
    }

}
