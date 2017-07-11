package org.zalando.compass.resource;

import com.google.common.annotations.VisibleForTesting;
import org.springframework.http.ResponseEntity;

import java.net.URI;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;

// TODO LinkService?!
@VisibleForTesting
public class Linking {

    public static <T> URI link(final ResponseEntity<T> entity) {
        return linkTo(entity).toUri();
    }

}
