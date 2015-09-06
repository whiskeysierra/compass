package org.zalando.compass.core;

import org.zalando.compass.api.DimensionId;
import org.zalando.compass.api.Entry;

import java.util.Comparator;

final class EntryComparator<T> implements Comparator<Entry<T>> {

    private final Comparator<DimensionId> delegate;

    EntryComparator(Comparator<DimensionId> delegate) {
        this.delegate = delegate;
    }

    @Override
    public int compare(Entry<T> left, Entry<T> right) {
        
        left.getDimensions();
        right.getDimensions();
        
        return 0;
    }

}
