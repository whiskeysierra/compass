package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Dimension;

import javax.annotation.Nullable;
import java.util.List;

public interface DimensionService {

    boolean replace(Dimension dimension);

    Dimension read(String id);

    List<Dimension> readAll(@Nullable String term);

    void delete(String id);

}
