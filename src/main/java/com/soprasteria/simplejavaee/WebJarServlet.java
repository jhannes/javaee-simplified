package com.soprasteria.simplejavaee;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.servlet.ServletHolder;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class WebJarServlet extends HttpServlet {
    private final String resourcePrefix;

    public WebJarServlet(String webjarName) {
        var properties = new Properties();

        try (var propFile = getClass().getResourceAsStream("/META-INF/maven/org.webjars/" + webjarName + "/pom.properties")) {
            properties.load(propFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var version = properties.getProperty("version");
        this.resourcePrefix = "/META-INF/resources/webjars/" + webjarName + "/" + version;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        var pathInfo = req.getPathInfo();
        try (var resourceAsStream = getClass().getResourceAsStream(resourcePrefix + pathInfo)) {
            if (resourceAsStream != null) {
                resourceAsStream.transferTo(resp.getOutputStream());
            } else {
                resp.sendError(404);
            }
        }
    }
}
