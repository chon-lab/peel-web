package net.peelweb.context.endpoint;

import net.peelweb.context.ResourceContext;

import java.util.HashMap;
import java.util.Map;

import static net.peelweb.util.WebUtils.getPathVariable;
import static net.peelweb.util.WebUtils.getPureResourceMapping;

public class EndpointTracker {

    private final ResourceContext resourceContext;

    private final String contextPath;

    private final Map<String, String> pathVariables;

    public EndpointTracker(ResourceContext resourceContext, String contextPath) {
        this.resourceContext = resourceContext;
        this.contextPath = contextPath;
        this.pathVariables = new HashMap<>();
    }

    public Endpoint find(StandardRequest standardRequest) {
        for (Endpoint endpoint : this.resourceContext.getEndpoints()) {
            if (!standardRequest.getMethod().equals(endpoint.getMethod())) {
                continue;
            }

            boolean mappingMatches = this.matches(endpoint, standardRequest.getUri());
            if (!mappingMatches) {
                continue;
            }

            standardRequest.setPathVariables(this.pathVariables);

            return endpoint;
        }

        return null;
    }

    private boolean matches(Endpoint endpoint, String requestUri) {
        requestUri = requestUri.substring(requestUri.indexOf(this.contextPath) + this.contextPath.length());

        requestUri = getPureResourceMapping(requestUri);
        String completeMapping = getPureResourceMapping(this.resourceContext.getBaseMapping()) + getPureResourceMapping(
                endpoint.getMapping());

        if (getPathVariable(endpoint.getMapping()) == null && completeMapping.equals(requestUri)) {
            return true;
        }

        String[] requestUriComponents = requestUri.split("/");
        String[] completeMappingComponents = completeMapping.split("/");

        if (requestUriComponents.length != completeMappingComponents.length) {
            return false;
        }

        for (int i = 0; i < requestUriComponents.length; i++) {
            String completeMappingComponent = completeMappingComponents[i];
            String requestUriComponent = requestUriComponents[i];

            String pathVariable = getPathVariable(completeMappingComponent);
            if (pathVariable == null && !requestUriComponent.equals(completeMappingComponent)) {
                return false;
            } else {
                this.pathVariables.put(pathVariable, requestUriComponent);
            }
        }

        return true;
    }

}
