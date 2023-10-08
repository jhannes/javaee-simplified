package com.soprasteria.infrastructure;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.RequestLog;
import org.eclipse.jetty.server.Response;
import org.logevents.optional.jakarta.HttpServletMDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import java.util.Set;

import static org.logevents.optional.jakarta.HttpServletResponseMDC.getMarker;

/**
 * Logs requests in Jetty to SLF4J using helpers from <a href="https://logevents.org">Logevents</a>
 * to set request and response properties on the MDC. This doesn't require logevents as your
 * slf4j implementation, but if you're using Logevents and logging with JSON, the events
 * will adhere to the
 * <a href="https://www.elastic.co/guide/en/ecs/current/index.html">Elastic Common Schema</a> specification.
 */
public class Slf4jHttpRequestLog implements RequestLog {

    private static final Logger log = LoggerFactory.getLogger(Slf4jHttpRequestLog.class);

    private static final Set<Integer> REDIRECT_STATUS_CODES = Set.of(301, 302, 303, 307, 308);

    @Override
    public final void log(Request req, Response resp) {
        try (var ignored = HttpServletMDC.put(req, resp)) {
            log.atLevel(getLevel(resp)).addMarker(getMarker(resp))
                    .log("{} {} {}", resp.getStatus(), req.getMethod(), req.getRequestURL());
        }
    }

    protected static boolean isRedirect(int statusCode) {
        return REDIRECT_STATUS_CODES.contains(statusCode);
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
