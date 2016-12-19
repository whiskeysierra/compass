package org.zalando.compass.library;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.zalando.jackson.datatype.money.MoneyModule;
import org.zalando.jackson.module.unknownproperty.UnknownPropertyModule;
import org.zalando.problem.ProblemModule;

@Configuration
public class JacksonConfiguration {

    @Bean
    @Primary
    public static ObjectMapper jacksonObjectMapper() {
        final ObjectMapper mapper = new ObjectMapper();

        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mapper.disable(SerializationFeature.WRITE_NULL_MAP_VALUES);
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

        mapper.enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
        mapper.enable(DeserializationFeature.USE_BIG_DECIMAL_FOR_FLOATS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
        mapper.disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE);

        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        mapper.enable(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY);
        mapper.disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

        writeBigDecimalAsPlain(mapper);

        mapper.registerModule(new AfterburnerModule());
        mapper.registerModule(new GuavaModule());
        mapper.registerModule(new JavaTimeModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new MoneyModule());
        mapper.registerModule(new ParameterNamesModule());
        mapper.registerModule(new ProblemModule());
        mapper.registerModule(new UnknownPropertyModule());

        mapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

        return mapper;
    }

    @SuppressWarnings("deprecation")
    private static void writeBigDecimalAsPlain(final ObjectMapper mapper) {
        mapper.enable(SerializationFeature.WRITE_BIGDECIMAL_AS_PLAIN);
    }

}
