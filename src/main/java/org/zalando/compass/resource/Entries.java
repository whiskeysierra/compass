package org.zalando.compass.resource;

import com.google.common.collect.ImmutableMap;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.zalando.compass.domain.model.Value;

import java.util.List;

@Getter
@AllArgsConstructor
class Entries {

    private final ImmutableMap<String, Entry> entries;

    @Getter
    @AllArgsConstructor
    public static class Entry {
        private final List<Value> values;
    }

}
