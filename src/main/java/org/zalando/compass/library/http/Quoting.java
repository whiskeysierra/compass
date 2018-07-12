package org.zalando.compass.library.http;

final class Quoting {

    private Quoting() {

    }

    static String quote(final String s) {
        return "\"" + s + "\"";
    }

}
