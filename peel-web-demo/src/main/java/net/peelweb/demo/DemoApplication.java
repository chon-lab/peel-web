package net.peelweb.demo;

import net.peelweb.PeelApp;
import net.peelweb.PeelAppBuilder;

public class DemoApplication {

    public static void main(String[] args) {
        PeelApp app = PeelAppBuilder.run(builder -> builder
                .context("/peel")
                .port(8080)
                .staticContentPath("net/peelweb/demo/static")
                .addController(new DemoController())
        );
        app.start();
    }

}
