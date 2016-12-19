package org.zalando.compass.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.annotations.VisibleForTesting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.Jaxb2RootElementHttpMessageConverter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.zalando.twintip.spring.SchemaResource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@VisibleForTesting
@ConditionalOnWebApplication
@Import(SchemaResource.class)
public class MvcConfiguration extends WebMvcConfigurationSupport {

    private final ObjectMapper mapper;

    @Autowired
    public MvcConfiguration(final ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void configureHandlerExceptionResolvers(final List<HandlerExceptionResolver> exceptionResolvers) {
        exceptionResolvers.add((request, response, handler, exception) -> null); // TODO: WHY DO WE NEED THIS SHIT?
        addDefaultHandlerExceptionResolvers(exceptionResolvers);
    }

    @Override
    public void configureMessageConverters(final List<HttpMessageConverter<?>> converters) {
        converters.addAll(messageConverters().getConverters().stream().collect(Collectors.toList()));
    }

    @Override
    public void addResourceHandlers(final ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/.well-known/**").addResourceLocations("classpath:/public/");
    }

    @Bean
    public HttpMessageConverters messageConverters() {
        return new HttpMessageConverters(false, Arrays.asList(
                textConverter(),
                new MappingJackson2HttpMessageConverter(mapper),
                new Jaxb2RootElementHttpMessageConverter()));
    }

    private StringHttpMessageConverter textConverter() {
        final StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        stringHttpMessageConverter.setWriteAcceptCharset(false);
        return stringHttpMessageConverter;
    }

}
