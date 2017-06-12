package org.zalando.compass.resource;

import com.google.common.collect.ImmutableMap;
import org.zalando.compass.domain.model.Value;

import java.util.List;

@lombok.Value
public class Entries {

    ImmutableMap<String, Entry> entries;

    @lombok.Value
    public static class Entry {
        List<Value> values;
    }

}
