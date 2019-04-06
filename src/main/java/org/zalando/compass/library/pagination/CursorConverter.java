package org.zalando.compass.library.pagination;

import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import java.util.Set;

@Component
@SuppressWarnings("UnstableApiUsage") // TypeToken
final class CursorConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return ImmutableSet.of(new ConvertiblePair(String.class, Cursor.class));
    }

    @Nullable
    @Override
    public Object convert(@Nullable Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        if (source == null) {
            return null;
        }

        final TypeToken<?> pivot = TypeToken.of(targetType.getResolvableType().getGeneric(0).getType());
        final TypeToken<?> query = TypeToken.of(targetType.getResolvableType().getGeneric(1).getType());

        return Cursor.valueOf(source.toString(), pivot, query);
    }

}
