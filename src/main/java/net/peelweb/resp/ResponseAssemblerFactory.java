package net.peelweb.resp;

import lombok.RequiredArgsConstructor;
import net.peelweb.enums.HttpContentType;
import net.peelweb.json.JsonConverterManager;

@RequiredArgsConstructor
public class ResponseAssemblerFactory {

    private final String staticContentPath;

    public ResponseAssembler get(HttpContentType httpContentType) {
        if (httpContentType.equals(HttpContentType.JSON)) {
            return new JsonResponseAssembler(JsonConverterManager.getInstance().getConverter());
        } else {
            if (this.staticContentPath == null) {
                return Object::toString;
            }
            return new StaticContentAssembler(this.staticContentPath);
        }
    }

}
