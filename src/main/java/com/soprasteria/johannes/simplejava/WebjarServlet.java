package com.soprasteria.johannes.simplejava;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.HttpContent;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.CachedContentFactory;
import org.eclipse.jetty.server.ResourceService;
import org.eclipse.jetty.util.resource.Resource;

import java.io.IOException;
import java.util.Properties;

public class WebjarServlet extends HttpServlet {
    private final ResourceService resourceService = new ResourceService();

    public WebjarServlet(HttpContent.ContentFactory contentFactory) {
        resourceService.setContentFactory(contentFactory);
        resourceService.setPathInfoOnly(true);
    }

    public WebjarServlet(@NonNull Resource webjarResource) {
        this(new CachedContentFactory(null, webjarResource, new MimeTypes(), true, true, new CompressedContentFormat[0]));
    }

    public WebjarServlet(String webjar) {
        this(getWebjarResource(webjar));
    }

    @SneakyThrows(IOException.class)
    private static Resource getWebjarResource(String artifactId) {
        var propertiesResource = Resource.newClassPathResource("META-INF/maven/org.webjars")
                .getResource(artifactId).getResource("/pom.properties");
        var properties = new Properties();
        try (var inputStream = propertiesResource.getInputStream()) {
            properties.load(inputStream);
        }
        return Resource.newClassPathResource("META-INF/resources/webjars")
                .getResource(artifactId).getResource(properties.getProperty("version"));
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }
}
