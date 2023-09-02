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
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

/**
 * This offers an alternative to Jetty's {@link org.eclipse.jetty.servlet.DefaultServlet} with the following
 * benefits:
 * <ul>
 *     <li>The path to the assets are specified in the constructor, meaning less magic</li>
 *     <li>If the src/main/resources is visible in the current path, this servlet enters development mode,
 *     where it prefers the version under src. This provides a faster feedback loop</li>
 *     <li>it supports a default resource, which is used for browser routed SPA applications</li>
 * </ul>
 */
public class ContentServlet extends HttpServlet {

    private final ResourceService resourceService = new ResourceService();

    public ContentServlet(String path) {
        var sourceResource = Resource.newResource(Path.of("src", "main", "resources", path));
        var targetResource = Resource.newClassPathResource(path);
        if (sourceResource.isDirectory()) {
            var resources = new ResourceCollection(sourceResource, targetResource, new DefaultResource(targetResource));
            resourceService.setContentFactory(new CachedContentFactory(null, resources, new MimeTypes(), false, false, new CompressedContentFormat[0]));

        } else {
            var resources = new ResourceCollection(targetResource, new DefaultResource(targetResource));
            resourceService.setContentFactory(new CachedContentFactory(null, resources, new MimeTypes(), true, false, new CompressedContentFormat[0]));
        }
        resourceService.setWelcomeFactory(pathInContext -> URIUtil.addPaths(pathInContext, "index.html"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }

    public static class DefaultResource extends PathResource {
        private final Resource targetResource;

        public DefaultResource(Resource targetResource) {
            super(getFile(targetResource));
            this.targetResource = targetResource;
        }

        private static File getFile(Resource targetResource) {
            try {
                return targetResource.getFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Resource addPath(String subPath) throws IOException {
            if (subPath.endsWith(".ico") || subPath.endsWith(".jpg") || subPath.endsWith(".png")) {
                return targetResource.getResource(subPath);
            }
            return targetResource.getResource("index.html");
        }
    }
}
