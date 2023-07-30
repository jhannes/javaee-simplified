package com.soprasteria.simplejavaee.infrastructure;

import jakarta.servlet.UnavailableException;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.util.resource.ResourceFactory;

import java.io.IOException;
import java.nio.file.Path;

public class PatchedDefaultServlet extends org.eclipse.jetty.servlet.DefaultServlet {
    private final ResourceFactory resources;
    private boolean disableBuffer = false;

    public PatchedDefaultServlet(Resource baseResource, Resource sourceResource) {
        if (sourceResource.exists()) {
            this.resources = new ResourceCollection(sourceResource, baseResource) {
                @Override
                public Resource addPath(String path) throws IOException {
                    return super.addPath(path);
                }
            };
            disableBuffer = true;
        } else {
            resources = baseResource;
        }
    }

    public PatchedDefaultServlet(String path) {
        this(getBaseResource(path), getSourceResource(path));
    }

    private static Resource getSourceResource(String path) {
        return Resource.newResource(Path.of("srcs", "main", "resources", path));
    }

    private static Resource getBaseResource(String path) {
        var baseUrl = Resource.class.getResource(path);
        if (baseUrl == null) {
            throw new IllegalArgumentException("Path not found in classpath " + path);
        }
        return Resource.newResource(baseUrl);
    }

    @Override
    public Resource getResource(String pathInContext) {
        try {
            Resource resource = resources.getResource(pathInContext);
            if (resource != null && resource.exists()) {
                return resource;
            }
            return resources.getResource("/index.html");
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void init() throws UnavailableException {
        if (disableBuffer) {
            getServletContext().setInitParameter("useFileMappedBuffer", "false");
        }
        super.init();
        ContextHandler.getCurrentContext().getContextHandler().getMimeTypes()
                .addMimeMapping("geojson", "application/json");
    }
}
