package net.peelweb.context.endpoint;

public interface RequestHandler {

    Response execute(StandardRequest standardRequest);

}
