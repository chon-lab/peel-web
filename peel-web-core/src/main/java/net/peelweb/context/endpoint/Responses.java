package net.peelweb.context.endpoint;

import net.peelweb.enums.HttpContentType;
import net.peelweb.enums.HttpResponseCode;

public class Responses {

    public static Response ok(Object body) {
        return new Response(HttpResponseCode.OK, body, HttpContentType.JSON);
    }

    public static Response page(String filePath) {
        return new Response(HttpResponseCode.OK, filePath, HttpContentType.HTML);
    }

    public static Response created(Object body) {
        return new Response(HttpResponseCode.CREATED, body, HttpContentType.JSON);
    }

    public static Response badRequest() {
        return new Response(HttpResponseCode.BAD_REQUEST, HttpContentType.JSON);
    }

    public static Response notFound() {
        return new Response(HttpResponseCode.NOT_FOUND, HttpContentType.JSON);
    }

    public static Response internalServerError() {
        return new Response(HttpResponseCode.INTERNAL_SERVER_ERROR, HttpContentType.JSON);
    }

    public static Response mount(HttpResponseCode code, Object body, HttpContentType httpContentType) {
        return new Response(code, body, httpContentType);
    }

    public static Response mount(HttpResponseCode code) {
        return new Response(code, null, null);
    }

}
