package com.soprasteria.simplejavaee;

import com.soprasteria.simplejavaee.api.jaxrs.LoginController;
import jakarta.json.Json;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Authentication;
import org.eclipse.jetty.server.Request;
import org.fluentjdbc.DbContext;
import org.logevents.optional.jakarta.HttpServletRequestMDC;
import org.slf4j.MDC;

import javax.security.auth.Subject;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

/**
 * Sets up the slf4j MDC with request values and populates the request with the current user
 * principal (if any). This uses MDC helpers from
 * <a href="https://logevents.org">Logevents</a>. This doesn't require logevents as your
 * slf4j implementation, but if you're using Logevents and logging with JSON, the events
 * will adhere to the
 * <a href="https://www.elastic.co/guide/en/ecs/current/index.html">Elastic Common Schema</a>
 * specification.
 */
public class ApplicationFilter implements Filter {
    private final ApplicationConfig config;
    private final DbContext dbContext;
    private final DataSource dataSource;

    public ApplicationFilter(ApplicationConfig config, DbContext dbContext, DataSource dataSource) {
        this.config = config;
        this.dbContext = dbContext;
        this.dataSource = dataSource;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MDC.clear();
        // Set up MDC variables in case `getAuthentication` throws an exception
        HttpServletRequestMDC.put(request);
        ((Request) request).setAuthentication(getAuthentication((Request) request));
        try (var ignored = HttpServletRequestMDC.put(request)) {
            try (var ignored2 = dbContext.startConnection(dataSource::getConnection)) {
                chain.doFilter(request, response);
            }
        }
    }

    private Authentication getAuthentication(Request request) {
        return getCookie(request, LoginController.ACCESS_TOKEN_COOKIE)
                .flatMap(this::getUserPrincipalFromAccessToken)
                .map(this::createAuthentication)
                .orElse(null);
    }

    private Optional<Principal> getUserPrincipalFromAccessToken(String accessToken) {
        try {
            var discoveryApi = config.getDiscoveryDocumentDto();
            var connection = (HttpURLConnection) new URL(discoveryApi.getString("userinfo_endpoint")).openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            if (connection.getResponseCode() == 401) {
                return Optional.empty();
            }
            if (connection.getResponseCode() >= 300) {
                throw new RuntimeException("Unsuccessful http request " + connection.getResponseCode() + " " + connection.getResponseMessage());
            }
            var userInfo = Json.createReader(connection.getInputStream()).readObject();
            return Optional.of(new ApplicationUserPrincipal(userInfo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private UserAuthentication createAuthentication(Principal userPrincipal) {
        return new UserAuthentication("accessToken",
                new DefaultUserIdentity(new Subject(false, Set.of(userPrincipal), Set.of(), Set.of()), userPrincipal, new String[0])
        );
    }

    private static Optional<String> getCookie(Request request, String cookieName) {
        if (request.getCookies() == null) {
            return Optional.empty();
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(cookieName))
                .findFirst()
                .map(Cookie::getValue);
    }

}
