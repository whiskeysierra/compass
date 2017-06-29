package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Dimension;
import org.zalando.compass.domain.model.DimensionRevision;

import javax.annotation.Nullable;
import java.util.List;

public interface DimensionService {

    boolean replace(Dimension dimension);

    List<Dimension> readAll(@Nullable String term);

    Dimension read(String id);

    List<DimensionRevision> readRevisions(String id);

    DimensionRevision readRevision(String id, long revision);

    void delete(String id);
}
