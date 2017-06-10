package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Dimension;

import java.util.List;
import java.util.Set;

public interface DimensionService {

    boolean replace(Dimension dimension);

    Dimension read(String id);

    List<Dimension> readAll(Set<String> ids);

    List<Dimension> readAll();

    void delete(String id);

}
