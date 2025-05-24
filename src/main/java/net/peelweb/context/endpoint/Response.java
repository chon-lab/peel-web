package net.peelweb.context.endpoint;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.peelweb.enums.HttpContentType;
import net.peelweb.enums.HttpResponseCode;

@AllArgsConstructor
@Getter
public class Response {

    private HttpResponseCode responseCode;

    private Object body;

    private HttpContentType httpContentType;

    protected Response(HttpResponseCode responseCode, HttpContentType httpContentType) {
        this.responseCode = responseCode;
        this.httpContentType = httpContentType;
    }

}
