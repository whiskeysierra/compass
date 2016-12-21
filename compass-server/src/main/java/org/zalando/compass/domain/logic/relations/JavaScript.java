package org.zalando.compass.domain.logic.relations;

import org.slf4j.LoggerFactory;
import org.zalando.compass.domain.model.Relation;

import javax.annotation.Nullable;
import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.script.SimpleBindings;

public final class JavaScript implements Relation {

    private final ScriptEngine engine = new ScriptEngineManager()
            .getEngineByMimeType("application/javascript");

    public JavaScript() {
        engine.put("log", LoggerFactory.getLogger(JavaScript.class));
    }

    @Override
    public String getId() {
        return "js";
    }

    @Override
    public String getTitle() {
        return "JavaScript";
    }

    @Override
    public String getDescription() {
        return "Matches values where the requested dimension values is satisfying the configured js script. " +
                "In case of multiple candidates it will match the least script (natural order).";
    }

    @Override
    public int compare(final String left, final String right) {
        return left.compareTo(right);
    }

    @Override
    public boolean test(final String configured, final String requested) {
        final Bindings bindings = new SimpleBindings();
        bindings.put("value", requested);

        try {
            @Nullable final Object result = engine.eval(configured, bindings);
            return Boolean.TRUE.equals(result);
        } catch (final ScriptException e) {
            throw new RuntimeException(e);
        }
    }

}
