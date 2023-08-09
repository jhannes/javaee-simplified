package com.soprasteria.johannes.simplejava;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.ws.rs.WebApplicationException;
import lombok.SneakyThrows;

import java.io.IOException;

public class ApplicationFilter implements Filter {
    @Override
    @SneakyThrows
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        try {
            chain.doFilter(request, response);
        } catch (WebApplicationException ex) {
            sendError(response, ex);
        } catch (Exception ex) {
            Throwable cause = ex;
            while (cause != null && cause != cause.getCause()) {
                if (cause instanceof WebApplicationException) {
                    sendError(response, ((WebApplicationException) cause));
                    return;
                } else if (cause.getCause() == null || cause.getCause() == cause) {
                    throw cause;
                } else {
                    cause = cause.getCause();
                }
            }
            throw ex;
        }
    }

    private static void sendError(ServletResponse response, WebApplicationException ex) throws IOException {
        ((HttpServletResponse)response).sendError(ex.getResponse().getStatus(), ex.getMessage());
    }
}
