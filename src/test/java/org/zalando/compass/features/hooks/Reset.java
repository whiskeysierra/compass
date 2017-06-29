package org.zalando.compass.features.hooks;

import cucumber.api.java.Before;
import org.jooq.DSLContext;
import org.jooq.Sequence;
import org.jooq.Table;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.zalando.tracer.aspectj.Traced;

import static org.zalando.compass.domain.persistence.model.Sequences.REVISION_ID_SEQ;
import static org.zalando.compass.domain.persistence.model.Sequences.VALUE_ID_SEQ;
import static org.zalando.compass.domain.persistence.model.Sequences.VALUE_INDEX_SEQ;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION;
import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.KEY;
import static org.zalando.compass.domain.persistence.model.Tables.KEY_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_DIMENSION_REVISION;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE_REVISION;

@Component
public class Reset {

    private final DSLContext db;

    @Autowired
    public Reset(final DSLContext db) {
        this.db = db;
    }

    @Traced
    @Before
    public void begin() {
        truncate(VALUE);
        truncate(KEY);
        truncate(DIMENSION);

        truncate(VALUE_DIMENSION_REVISION);
        truncate(VALUE_REVISION);
        truncate(KEY_REVISION);
        truncate(DIMENSION_REVISION);
        truncate(REVISION);

        restart(REVISION_ID_SEQ);
        restart(VALUE_ID_SEQ);
        restart(VALUE_INDEX_SEQ);
    }

    private void truncate(final Table<?> table) {
        db.truncate(table).cascade().execute();
    }

    private void restart(final Sequence<Long> sequence) {
        db.alterSequence(sequence).restart().execute();
    }

}
