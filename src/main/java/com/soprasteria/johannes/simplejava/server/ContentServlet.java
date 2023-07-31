package com.soprasteria.johannes.simplejava.server;

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
import java.nio.file.Files;
import java.nio.file.Path;

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
        var sourcePath = Path.of("src", "main", "resources", path);
        if (Files.exists(sourcePath)) {
            return new CachedContentFactory(null, Resource.newClassPathResource(path), new MimeTypes(), false, false, new CompressedContentFormat[0]);
        } else {
            return new CachedContentFactory(null, Resource.newClassPathResource(path), new MimeTypes(), true, false, new CompressedContentFormat[0]);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }
}
