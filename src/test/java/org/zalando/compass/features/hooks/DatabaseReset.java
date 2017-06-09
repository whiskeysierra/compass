package org.zalando.compass.features.hooks;

import cucumber.api.java.Before;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.stream.Stream;

import static org.zalando.compass.domain.persistence.model.Tables.DIMENSION;
import static org.zalando.compass.domain.persistence.model.Tables.KEY;
import static org.zalando.compass.domain.persistence.model.Tables.VALUE;

@Component
public class DatabaseReset {

    private final DSLContext db;

    @Autowired
    public DatabaseReset(final DSLContext db) {
        this.db = db;
    }

    @Before
    public void begin() {
        Stream.of(VALUE, DIMENSION, KEY).forEach(table ->
                db.truncate(table).cascade().execute());
    }

}
