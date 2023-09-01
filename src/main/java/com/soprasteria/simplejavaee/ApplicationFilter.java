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

import javax.security.auth.Subject;
import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Principal;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;

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
        ((Request)request).setAuthentication(getAuthentication((Request) request));
        try (var ignored = dbContext.startConnection(dataSource::getConnection)) {
            chain.doFilter(request, response);
        }
    }

    private Authentication getAuthentication(Request request) {
        var principal = getUserPrincipal(request);
        if (principal == null) {
            return null;
        }
        return new UserAuthentication("accessToken",
                new DefaultUserIdentity(new Subject(false, Set.of(principal), Set.of(), Set.of()), principal, new String[0])
        );
    }

    private Principal getUserPrincipal(Request request) {
        if (request.getCookies() == null) {
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(c -> c.getName().equals(LoginController.ACCESS_TOKEN_COOKIE))
                .findFirst()
                .map(Cookie::getValue)
                .flatMap(this::getUserPrincipalFromAccessToken)
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
                throw new IOException("Unsuccessful http request " + connection.getResponseCode() + " " + connection.getResponseMessage());
            }
            var userInfo = Json.createReader(connection.getInputStream()).readObject();
            return Optional.of(new ApplicationUserPrincipal(userInfo));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
