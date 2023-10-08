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
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.resource.ResourceFactory;

import java.io.IOException;
import java.nio.file.Path;

/**
 * This offers an alternative to Jetty's {@link org.eclipse.jetty.servlet.DefaultServlet} with the following
 * benefits:
 * <ul>
 *     <li>
 *         The path to the assets are specified in the constructor instead of through
 *         servlet init variables
 *     </li>
 *     <li>
 *         If the src/main/resources is visible in the current path, this servlet enters
 *         development mode, where it prefers the version under src. This provides a faster
 *         feedback loop as the project doesn't need to be build to serve the changes.
 *     </li>
 *     <li>
 *         When src/main/resources is visible in the current path, file mapped buffers are turned
 *         off, which avoids file locking problems on Windows.
 *     </li>
 *     <li>it supports a default resource, which is used for browser routed SPA applications</li>
 * </ul>
 */
public class ContentServlet extends HttpServlet {

    private final ResourceService resourceService = new ResourceService();

    public ContentServlet(String path) {
        resourceService.setContentFactory(createContentFactory(path));
        resourceService.setWelcomeFactory(dir -> URIUtil.addPaths(dir, "index.html"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }

    private CachedContentFactory createContentFactory(String path) {
        var sourceResource = Resource.newResource(Path.of("src", "main", "resources", path));
        var targetResource = Resource.newClassPathResource(path);
        var resourceCollection = sourceResource.exists()
                ? new ResourceCollection(sourceResource, targetResource)
                : targetResource;
        var resources = withDefaultResource(resourceCollection, "/index.html");
        // On Windows, Jetty lock file mapped buffer resources, preventing changes without restart
        var useFileMappedBuffer = !sourceResource.exists();
        return new CachedContentFactory(null, resources, new MimeTypes(), useFileMappedBuffer, false, new CompressedContentFormat[0]);
    }

    /**
     * Decorator on top of a ResourceFactory that returns a default resource when no match. Useful
     * with client side browser routing.
     */
    private static ResourceFactory withDefaultResource(ResourceFactory delegate, String defaultResourcePath) {
        return path -> {
            var resource = delegate.getResource(path);
            if (resource.exists()) {
                return resource;
            }
            if (path.endsWith(".ico") || path.endsWith(".jpg") || path.endsWith(".png")) {
                return resource;
            }
            return delegate.getResource(defaultResourcePath);
        };
    }

}
