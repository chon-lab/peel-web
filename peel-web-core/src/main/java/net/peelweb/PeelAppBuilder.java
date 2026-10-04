package net.peelweb;

import net.peelweb.context.ResourceContext;
import net.peelweb.context.controller.Controllers;
import net.peelweb.server.HttpServerHolder;
import net.peelweb.server.HttpServerHolderFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PeelAppBuilder {

    private final List<ResourceContext> resourceContexts;

    private String staticContentPath;

    private String contextPath;

    private int port;

    private boolean isExternalStaticContent;

    public PeelAppBuilder() {
        this.resourceContexts = new ArrayList<>();
    }

    public static PeelApp run(Consumer<PeelAppBuilder> config) {
        PeelAppBuilder builder = new PeelAppBuilder();
        config.accept(builder);
        return builder.build();
    }

    public PeelAppBuilder context(String context) {
        this.contextPath = context;
        return this;
    }

    public PeelAppBuilder port(int port) {
        this.port = port;
        return this;
    }

    public PeelAppBuilder addResourceContext(ResourceContext resourceContext) {
        this.resourceContexts.add(resourceContext);
        return this;
    }

    public PeelAppBuilder addController(Object controller) {
        this.resourceContexts.add(Controllers.from(controller));
        return this;
    }

    public PeelAppBuilder staticContentPath(String staticContentPath) {
        this.staticContentPath = staticContentPath;
        return this;
    }

    public PeelAppBuilder isExternalStaticContent() {
        this.isExternalStaticContent = true;
        return this;
    }

    private PeelApp build() {
        HttpServerHolder httpServerHolder = HttpServerHolderFactory.release(this.resourceContexts,
                this.staticContentPath, this.isExternalStaticContent, this.contextPath, this.port);
        return new PeelApp(httpServerHolder);
    }

}
