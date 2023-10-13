package com.soprasteria.johannes;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class MyLittleServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        var pathInfo = req.getPathInfo();
        var author = req.getParameter("author");
        resp.setContentType("text/plain");

        if ("/books".equals(pathInfo)) {
            resp.getWriter().write("Books by " + author);
        } else {
            resp.getWriter().write("<h1>Hello World</h1>");
        }
    }
}
