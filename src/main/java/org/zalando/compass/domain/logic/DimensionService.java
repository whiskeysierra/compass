package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Dimension;

public interface DimensionService {

    boolean replace(Dimension dimension);
    void delete(String id);

}
