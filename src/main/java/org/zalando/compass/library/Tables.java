package org.zalando.compass.library;

import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.Table;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.jooq.impl.DSL.row;
import static org.jooq.impl.DSL.val;
import static org.jooq.impl.DSL.values;

public final class Tables {

    public static <K, V> Table<Record2<K, V>> table(final Map<K, V> dimensions,
            final Class<K> keyType, final Class<V> valueType) {

        final List<Row2<K, V>> rows = new ArrayList<>(dimensions.size());

        dimensions.forEach((key, value) ->
                rows.add(row(val(key, keyType), val(value, valueType))));

        @SuppressWarnings({"unchecked", "rawtypes"})
        final Row2<K, V>[] array = rows.toArray(new Row2[rows.size()]);

        return values(array);
    }
    
}
