package com.soprasteria.infrastructure;

import jakarta.json.Json;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public class SQLExceptionMapper implements ExceptionMapper<SQLException> {

    private static final Logger log = LoggerFactory.getLogger(SQLExceptionMapper.class);

    @Override
    public Response toResponse(SQLException exception) {
        log.error("SQLException", exception);
        return Response.serverError()
                .header("X-Exception", exception.toString())
                .entity(Json.createObjectBuilder()
                        .add("error", exception.getClass().getName())
                        .add("errorDetail", exception.toString())
                        .build()
                ).build();
    }
}
