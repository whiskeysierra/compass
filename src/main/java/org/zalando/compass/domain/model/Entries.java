package org.zalando.compass.domain.model;

import com.google.common.collect.Multimap;

@lombok.Value
public class Entries {

    private final Multimap<String, Value> entries;

}
