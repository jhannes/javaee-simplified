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

import java.io.IOException;
import java.nio.file.Path;

public class ContentServlet extends HttpServlet {

    private final ResourceService resourceService = new ResourceService();

    public ContentServlet(String path) {
        var sourceResource = Resource.newResource(Path.of("src", "main", "resources", path));
        var targetResource = Resource.newClassPathResource(path);
        if (sourceResource.isDirectory()) {
            var resources = new ResourceCollection(sourceResource, targetResource);
            resourceService.setContentFactory(new CachedContentFactory(null, resources, new MimeTypes(), false, false, new CompressedContentFormat[0]));

        } else {
            resourceService.setContentFactory(new CachedContentFactory(null, targetResource, new MimeTypes(), true, false, new CompressedContentFormat[0]));
        }
        resourceService.setWelcomeFactory(pathInContext -> URIUtil.addPaths(pathInContext, "index.html"));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }
}
