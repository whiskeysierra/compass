package org.zalando.compass.resource;

import com.google.common.collect.Multimap;
import org.zalando.compass.domain.model.Value;

@lombok.Value
public class Entries {

    private final Multimap<String, Value> entries;

}
