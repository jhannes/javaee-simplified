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

/**
 * Serve static content structured in a <a href="https://www.webjars.org/">webjar</a> format.
 * A webjar will have its resources located in the classpath under
 * <code>/META-INF/resources/webjars/<i>artifactId</i>/<i>version</i></code>.
 * To determine the <code>version</code> at runtime, we read the properties file
 * <code>/META-INF/maven/org.webjars/<i>artifactId</i>/pom.properties</code>
 */
public class WebJarServlet extends HttpServlet {
    private final ResourceService resourceService = new ResourceService();

    public WebJarServlet(String artifactId) {
        this(artifactId, findWebjarVersion(artifactId));
    }

    public WebJarServlet(String artifactId, String version) {
        var targetResource = getWebJarResource(artifactId, version);
        resourceService.setContentFactory(new CachedContentFactory(null, targetResource, new MimeTypes(), true, false, new CompressedContentFormat[0]));
        resourceService.setWelcomeFactory(pathInContext -> URIUtil.addPaths(pathInContext, "index.html"));
        resourceService.setPathInfoOnly(true);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }

    private Resource getWebJarResource(String artifactId, String version) {
        return Resource.newClassPathResource(
                "/META-INF/resources/webjars/%s/%s".formatted(artifactId, version)
        );
    }

    private static String findWebjarVersion(String artifactId) {
        var jarPropertiesName = "/META-INF/maven/org.webjars/%s/pom.properties".formatted(artifactId);
        try (var inputStream = WebJarServlet.class.getResourceAsStream(jarPropertiesName)) {
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
