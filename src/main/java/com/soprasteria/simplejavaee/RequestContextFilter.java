package com.soprasteria.simplejavaee;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.eclipse.jetty.security.DefaultUserIdentity;
import org.eclipse.jetty.security.UserAuthentication;
import org.eclipse.jetty.server.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.security.auth.Subject;
import java.io.IOException;
import java.security.Principal;
import java.util.Optional;
import java.util.Set;

public class RequestContextFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(RequestContextFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        MDC.clear();
        var request = (Request) req;
        getPrincipal()
                .map(RequestContextFilter::getUserAuthentication)
                .ifPresent(request::setAuthentication);
        MDC.put("url", request.getRequestURL().toString());
        MDC.put("remoteUser", request.getRemoteUser());
        log.info("Something happened");
        chain.doFilter(req, response);
    }

    private static Optional<Principal> getPrincipal() {
        return Optional.of(() -> "A Secret Name");
    }

    private static UserAuthentication getUserAuthentication(Principal principal) {
        return new UserAuthentication("ldap",
                new DefaultUserIdentity(new Subject(false, Set.of(principal), Set.of(), Set.of()), principal, new String[0])
        );
    }
}
