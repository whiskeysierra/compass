package org.zalando.compass.domain.logic;

import org.zalando.compass.domain.model.Relation;

public final class Equality implements Relation {

    @Override
    public String getId() {
        return "=";
    }

    @Override
    public String getDescription() {
        return "Equality";
    }

}
