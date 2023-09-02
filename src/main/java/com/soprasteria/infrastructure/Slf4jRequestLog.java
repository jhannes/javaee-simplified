package com.soprasteria.infrastructure;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.logevents.optional.jakarta.HttpServletMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import static org.logevents.optional.jakarta.HttpServletResponseMDC.getMarker;

public class Slf4jRequestLog implements RequestLog {

    private static final Logger log = LoggerFactory.getLogger(Slf4jRequestLog.class);
    @Override
    public final void log(Request req, Response resp) {
        try (var ignored = HttpServletMDC.put(req, resp)) {
            log.atLevel(getLevel(resp)).addMarker(getMarker(resp))
                    .log("{} {} {}", resp.getStatus(), req.getMethod(), req.getRequestURL());
        }
    }

    protected static boolean isRedirect(int status) {
        return status == 301 || status == 302 || status == 303 || status == 307 || status == 308;
    }

    protected Level getLevel(Response response) {
        if (response.getStatus() >= 500) {
            return Level.WARN;
        } else if (response.getStatus() >= 400 || isRedirect(response.getStatus())) {
            return Level.INFO;
        } else if (response.getStatus() < 100) {
            return Level.ERROR;
        } else {
            return Level.DEBUG;
        }
    }
}
