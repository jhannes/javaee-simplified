package com.soprasteria.javaeesimplified;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.fluentjdbc.DbContext;

import javax.sql.DataSource;
import java.io.IOException;

public class ApplicationServletFilter implements Filter {
    private final DbContext dbContext;
    private final DataSource dataSource;

    public ApplicationServletFilter(DbContext dbContext, DataSource dataSource) {
        this.dbContext = dbContext;
        this.dataSource = dataSource;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try (var ignored = dbContext.startConnection(dataSource)) {
            chain.doFilter(request, response);
        }
    }
}
