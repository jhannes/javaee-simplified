package com.soprasteria.simplejavaee;

import com.soprasteria.simplejavaee.infrastructure.ApplicationDataSource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;

public class ApplicationFilter implements Filter {
    private final DataSource dataSource;

    public ApplicationFilter(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try (var ignored = ApplicationDataSource.beginConnection(dataSource)) {
            chain.doFilter(request, response);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
