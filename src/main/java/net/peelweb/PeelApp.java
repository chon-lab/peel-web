package net.peelweb;

import lombok.RequiredArgsConstructor;
import net.peelweb.server.HttpServerHolder;

@RequiredArgsConstructor
public class PeelApp {

    private final HttpServerHolder serverHolder;

    public void start() {
        this.serverHolder.start();
    }

    public void stop() {
        this.serverHolder.stop();
    }

}
