package com.soprasteria.johannes.simplejava.server;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
public class ContentServlet extends HttpServlet {

    private final ResourceService resourceService = new ResourceService();

    public ContentServlet(CachedContentFactory contentFactory) {
        resourceService.setContentFactory(contentFactory);
        resourceService.setWelcomeFactory(pathInContext -> URIUtil.addPaths(pathInContext, "index.html"));
    }

    public ContentServlet(String path) {
        this(createContentFactory(path));
    }

    private static CachedContentFactory createContentFactory(String path) {
        var sourceResource = Resource.newResource(Path.of("srsc", "main", "resources", path));
        var targetResource = Resource.newClassPathResource(path);
        if (sourceResource.exists()) {
            return createContentFactory(new ResourceCollection(sourceResource, targetResource), false);
        } else {
            return createContentFactory(targetResource, true);
        }
    }

    private static CachedContentFactory createContentFactory(ResourceFactory resources, boolean useFileMappedBuffer) {
        return new CachedContentFactory(null, resources, new MimeTypes(), useFileMappedBuffer, false, new CompressedContentFormat[0]);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }
}
