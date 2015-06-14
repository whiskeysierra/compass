package org.whiskeysierra.compass.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.reflect.TypeParameter;
import com.google.common.reflect.TypeToken;
import org.whiskeysierra.compass.api.Node;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

public final class NodeReader {
    
    private final ObjectMapper mapper;

    public NodeReader(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> Node<T> read(InputStream stream, Class<T> type) throws IOException {
        return read(stream, TypeToken.of(type));
    }
    
    public <T> Node<T> read(InputStream stream, TypeToken<T> type) throws IOException {
        // TODO validate variants:
        // dimension+nodes+value
        // dimension+nodes+!value
        // !dimensions+!nodes+value
        
        return mapper.readValue(stream, new TypeReference<Node<T>>() {
            @Override
            public Type getType() {
                return new TypeToken<Node<T>>() {}
                        .where(new TypeParameter<T>() {}, type)
                        .getType();
            }
        });
    }

}
