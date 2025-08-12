package net.peelweb.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import lombok.RequiredArgsConstructor;
import net.peelweb.context.ResourceContext;
import net.peelweb.context.endpoint.*;
import net.peelweb.enums.HttpContentType;
import net.peelweb.enums.HttpHeader;
import net.peelweb.enums.HttpMethod;
import net.peelweb.enums.HttpResponseCode;
import net.peelweb.resp.ResponseAssembler;
import net.peelweb.resp.ResponseAssemblerFactory;
import net.peelweb.util.WebUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class ComSunNetHttpServerHolder implements HttpServerHolder {

    private final List<ResourceContext> resourceContexts;

    private final int port;

    private final String contextPath;

    private final String staticContentPath;

    private HttpServer httpServer;

    private String getRequestBody(HttpExchange exchange) throws IOException {
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream();
             InputStream input = exchange.getRequestBody()) {
            byte[] data = new byte[1024];
            int bytesRead;
            while ((bytesRead = input.read(data)) != -1) {
                buffer.write(data, 0, bytesRead);
            }
            return buffer.toString().isEmpty() ? null : buffer.toString();
        }
    }

    private void setDefaultHeaders(ResourceContext ResourceContext, HttpExchange exchange) {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods",
                ResourceContext.getEndpoints().stream().map(e -> e.getMethod().toString())
                        .collect(Collectors.joining(",")));
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers",
                "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, "
                        + "Access-Control-Request-Method, "
                        + "Access-Control-Request-Headers, Authorization, Access-Control-Allow-Origin");
    }

    private String mountResourceContextMapping(ResourceContext resourceContext) {
        if (this.contextPath == null || this.contextPath.isEmpty()) {
            return resourceContext.getBaseMapping();
        }
        return this.contextPath + resourceContext.getBaseMapping();
    }

    @Override
    public void start() {
        if (this.httpServer != null) {
            return;
        }

        try {
            this.httpServer = HttpServer.create(new InetSocketAddress(this.port), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        this.httpServer.setExecutor(Executors.newCachedThreadPool());
        this.httpServer.start();

        final ResponseAssemblerFactory responseAssemblerFactory = new ResponseAssemblerFactory(this.staticContentPath);

        for (ResourceContext resourceContext : this.resourceContexts) {
            EndpointTracker endpointTracker = new EndpointTracker(resourceContext, this.contextPath);

            String mapping = this.mountResourceContextMapping(resourceContext);
            this.httpServer.createContext(mapping, exchange -> {
                String requestMethod = exchange.getRequestMethod();

                this.setDefaultHeaders(resourceContext, exchange);

                if (requestMethod.equalsIgnoreCase(HttpMethod.OPTIONS.toString())) {
                    exchange.sendResponseHeaders(HttpResponseCode.OK.getNumber(), -1);
                    return;
                }

                Map<String, String> queryParameters = WebUtils.getQueryParameters(exchange.getRequestURI().toString());
                List<String> contentType = exchange.getRequestHeaders().get(HttpHeader.CONTENT_TYPE.getName());
                Map<String, Object> formData;
                if (contentType != null && !contentType.isEmpty()) {
                    formData = WebUtils.getFormData(exchange.getRequestBody(), contentType.get(0));
                } else {
                    formData = new HashMap<>();
                }

                StandardRequest standardRequest = new StandardRequest(HttpMethod.get(requestMethod),
                        exchange.getRequestURI().toString(), this.getRequestBody(exchange), queryParameters, formData);
                Endpoint endpoint = endpointTracker.find(standardRequest);

                Response response;
                if (endpoint != null) {
                    try {
                        response = endpoint.getHandler().execute(standardRequest);
                    } catch (Exception e) {
                        response = Responses.mount(HttpResponseCode.INTERNAL_SERVER_ERROR);
                        e.printStackTrace();
                    }

                    if (response == null) {
                        response = Responses.mount(HttpResponseCode.NOT_IMPLEMENTED);
                    } else if (response.getBody() == null) {
                        response = Responses.mount(HttpResponseCode.NO_CONTENT);
                    }
                } else {
                    HttpContentType httpContentType = HttpContentType.fromUri(exchange.getRequestURI().toString());
                    if (httpContentType != null) {
                        response = Responses.mount(HttpResponseCode.OK, exchange.getRequestURI().toString(),
                                httpContentType);
                    } else {
                        exchange.sendResponseHeaders(HttpResponseCode.NOT_FOUND.getNumber(), 0);
                        return;
                    }
                }

                ResponseAssembler responseAssembler = responseAssemblerFactory.get(response.getHttpContentType());

                OutputStream outputBody = exchange.getResponseBody();
                byte[] body = responseAssembler.assembly(response.getBody()).getBytes(StandardCharsets.UTF_8);

                exchange.getResponseHeaders().add(HttpHeader.CONTENT_TYPE.getName(),
                        response.getHttpContentType().getName() + ";charset=utf-8");
                exchange.sendResponseHeaders(response.getResponseCode().getNumber(), body.length);

                outputBody.write(body);
                outputBody.close();
            });
        }
    }

    @Override
    public void stop() {
        this.httpServer.stop(0);
    }
}
