package com.soprasteria.simplejavaee;

import com.soprasteria.generated.openid.api.HttpDiscoveryApi;
import com.soprasteria.generated.openid.model.UserinfoDto;
import com.soprasteria.simplejavaee.api.LoginController;
import jakarta.json.JsonObject;
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
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.Principal;
import java.util.Arrays;
import java.util.Set;

public class ApplicationFilter implements Filter {
    private final ApplicationConfig config;
    private final DbContext dbContext;

    public ApplicationFilter(ApplicationConfig config, DbContext dbContext) {
        this.config = config;
        this.dbContext = dbContext;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ((Request)request).setAuthentication(getAuthentication((Request) request));
        chain.doFilter(request, response);
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
                .map(this::getUserPrincipalFromAccessToken)
                .orElse(null);
    }

    private Principal getUserPrincipalFromAccessToken(String accessToken) {
        try {
            var discoveryApi = new HttpDiscoveryApi(config.getIssuerUrl(), LoginController.openidJsonb);
            var connection = (HttpURLConnection) discoveryApi.getDiscoveryDocument().getUserinfoEndpoint().toURL().openConnection();
            connection.setRequestProperty("Authorization", "Bearer " + accessToken);
            if (connection.getResponseCode() >= 300) {
                throw new IOException("Unsuccessful http request " + connection.getResponseCode() + " " + connection.getResponseMessage());
            }
            var userInfoRaw = LoginController.openidJsonb.fromJson(connection.getInputStream(), JsonObject.class);
            var userInfo = LoginController.openidJsonb.fromJson(userInfoRaw.toString(), UserinfoDto.class);
            return new ApplicationUserPrincipal(userInfo);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
