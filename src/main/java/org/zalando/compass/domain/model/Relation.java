package org.zalando.compass.domain.model;

import java.util.Comparator;
import java.util.function.BiPredicate;

public interface Relation extends BiPredicate<String, String> {

    String getId();

    String getTitle();

    String getDescription();

}
