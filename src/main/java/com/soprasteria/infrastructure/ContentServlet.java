package com.soprasteria.infrastructure;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.eclipse.jetty.http.CompressedContentFormat;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.server.CachedContentFactory;
import org.eclipse.jetty.server.ResourceService;
import org.eclipse.jetty.util.resource.PathResource;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

/**
 * This offers an alternative to Jetty's {@link org.eclipse.jetty.servlet.DefaultServlet} with the following
 * benefits:
 * <ul>
 *     <li>
 *         The path to the assets are specified in the constructor instead of through an
 *         arbitrary servlet init variables
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
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }

    private CachedContentFactory createContentFactory(String path) {
        var sourceResource = new DirectoryResource(Path.of("src", "main", "resources", path));
        var targetResource = DirectoryResource.getClasspathResource(path);
        var resources = sourceResource.exists()
                ? new ResourceCollection(sourceResource, targetResource, new DefaultResource(targetResource))
                : new ResourceCollection(targetResource, new DefaultResource(targetResource));
        var useFileMappedBuffer = !sourceResource.exists();
        return new CachedContentFactory(null, resources, new MimeTypes(), useFileMappedBuffer, false, new CompressedContentFormat[0]);
    }

    /**
     * Overridden PathResource with deals with welcome files (index.html) directly.
     * Without this adjustment, {@link DefaultResource} will override welcome files.
     */
    public static class DirectoryResource extends PathResource {
        public DirectoryResource(Path path) {
            super(path);
        }

        public DirectoryResource(URL resource) throws IOException, URISyntaxException {
            super(Objects.requireNonNull(resource));
        }

        public static Resource getClasspathResource(String path) {
            try {
                return new DirectoryResource(DirectoryResource.class.getClassLoader().getResource(path));
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public Resource addPath(String subPath) throws IOException {
            var resource = super.addPath(subPath);
            if (resource.isDirectory()) {
                var welcomeFile = resource.addPath("index.html");
                if (welcomeFile.exists() && !welcomeFile.isDirectory()) {
                    return welcomeFile;
                }
            }
            return resource;
        }
    }

    /**
     * Returns the same default resource for the all requests. Used to support reload of
     * browser routed paths
     */
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
            if (subPath.startsWith("/api/") || subPath.endsWith(".ico") || subPath.endsWith(".jpg") || subPath.endsWith(".png")) {
                return targetResource.getResource(subPath);
            }
            return targetResource.getResource("index.html");
        }
    }
}
