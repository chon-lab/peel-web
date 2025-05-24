package net.peelweb;

public class Main {

    public static void main(String[] args) {
        PeelApp app = PeelAppBuilder.run(builder -> builder
                .context("/peel")
                .port(8080)
                .staticContentPath("src/main/resources/net/peelweb/static")
                .addController(new TestController())
        );
        app.start();
    }

}


