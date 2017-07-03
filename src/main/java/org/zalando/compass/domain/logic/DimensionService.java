package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;
import org.zalando.compass.domain.model.Page;

import javax.annotation.Nullable;
import java.util.List;

public interface DimensionService {

    boolean replace(Dimension dimension);

    List<Dimension> readAll(@Nullable String term);

    Dimension read(String id);

    Page<DimensionRevision> readRevisions(String id, int limit, @Nullable Long after);

    void delete(String id);

}
