package org.zalando.compass.api;

import com.google.common.collect.ListMultimap;

import javax.annotation.concurrent.Immutable;

@Immutable
public interface Values extends ListMultimap<KeyId, Entry<?>> {

}
