package net.peelweb.demo;

import net.peelweb.context.controller.Controller;
import net.peelweb.context.controller.Mapping;
import net.peelweb.context.endpoint.Request;
import net.peelweb.context.endpoint.Response;
import net.peelweb.context.endpoint.Responses;

@Controller
public class DemoController {

    @Mapping("/health")
    public Response health(Request request) {
        return Responses.ok("UP");
    }

    @Mapping("/demo/hello")
    public Response hello(Request request) {
        return Responses.ok("Hello from Peel Web");
    }

    @Mapping("/demo/static")
    public Response index(Request request) {
        return Responses.page("index.html");
    }

}
