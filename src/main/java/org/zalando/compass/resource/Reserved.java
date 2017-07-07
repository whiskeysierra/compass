package org.zalando.compass.resource;

import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

interface Reserved {

    @RequestMapping(path = {
            "/cursor",
            "/embed",
            "/fields",
            "/filter",
            "/key",
            "/limit",
            "/offset",
            "/q",
            "/query",
            "/revisions",
            "/sort",
    })
    default void reserved(final HttpServletRequest request) throws HttpRequestMethodNotSupportedException {
        throw new HttpRequestMethodNotSupportedException(request.getMethod(), "ID is reserved");
    }

}
