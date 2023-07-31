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
import org.eclipse.jetty.util.resource.ResourceFactory;

import java.io.IOException;

public class ContentServlet extends HttpServlet {

    private final ResourceService resourceService = new ResourceService();

    public ContentServlet(CachedContentFactory contentFactory) {
        resourceService.setContentFactory(contentFactory);
        resourceService.setWelcomeFactory(pathInContext -> URIUtil.addPaths(pathInContext, "index.html"));
    }

    public ContentServlet(ResourceFactory resource) {
        this(new CachedContentFactory(null, resource, new MimeTypes(), true, false, new CompressedContentFormat[0]));
    }

    public ContentServlet(String path) {
        this(Resource.newClassPathResource(path));
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        if (!resourceService.doGet(req, resp)) {
            resp.sendError(404);
        }
    }
}
