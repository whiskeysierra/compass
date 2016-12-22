package org.zalando.compass.domain.model;

import com.google.common.collect.Multimap;

import java.util.Map;

@lombok.Value
public class Entries {

    private final Multimap<String, Value> entries;

}
