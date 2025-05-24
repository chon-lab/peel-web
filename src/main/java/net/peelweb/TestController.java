package net.peelweb;

import net.peelweb.context.controller.Controller;
import net.peelweb.context.controller.Mapping;
import net.peelweb.context.endpoint.Request;
import net.peelweb.context.endpoint.Response;
import net.peelweb.context.endpoint.Responses;

import java.util.Arrays;

@Controller("/test")
public class TestController {

    @Mapping("/hello-world")
    public Response get(Request request) {
        return Responses.ok(Arrays.asList("Hello", "World!"));
    }

    @Mapping("/hello-world/static")
    public Response index(Request request) {
        return Responses.page("index.html");
    }

}


