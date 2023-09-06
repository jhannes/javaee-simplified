package com.soprasteria.infrastructure;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.CachedContentFactory;
import org.eclipse.jetty.server.ResourceService;
import org.eclipse.jetty.util.URIUtil;
import org.eclipse.jetty.util.resource.Resource;

import java.io.IOException;
import java.util.Properties;

public class WebJarServlet extends HttpServlet {
    private final ResourceService resourceService = new ResourceService();

    public WebJarServlet(String webjar) {
        var targetResource = getWebJarResource(webjar);
        resourceService.setContentFactory(new CachedContentFactory(null, targetResource, new MimeTypes(), true, false, new CompressedContentFormat[0]));
        resourceService.setWelcomeFactory(pathInContext -> URIUtil.addPaths(pathInContext, "index.html"));
        resourceService.setPathInfoOnly(true);
    }

    private Resource getWebJarResource(String webjar) {
        return Resource.newClassPathResource(
                "/META-INF/resources/webjars/%s/%s".formatted(webjar, findWebjarVersion(webjar)));
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }

    private String findWebjarVersion(String webjar) {
        var jarPropertiesName = "/META-INF/maven/org.webjars/%s/pom.properties".formatted(webjar);
        try (var inputStream = getClass().getResourceAsStream(jarPropertiesName)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("Not found " + jarPropertiesName);
            }
            var properties = new Properties();
            properties.load(inputStream);
            return properties.getProperty("version");
        } catch (IOException e) {
            throw new RuntimeException("While loading " + jarPropertiesName, e);
        }
    }
}
