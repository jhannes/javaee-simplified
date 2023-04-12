package com.soprasteria.simplejavaee.infrastructure;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDataSource {

    private static ThreadLocal<Connection> threadConnection = new ThreadLocal<>();
    private static DataSource dataSource;


    public static PreparedStatement prepareStatement(String sql) throws SQLException {
        return threadConnection.get().prepareStatement(sql);
    }

    public static <T> List<T> queryAllRows(PreparedStatement statement, ThrowingFunction<ResultSet, T, SQLException> mapper) throws SQLException {
        try (var rs = statement.executeQuery()) {
            var result = new ArrayList<T>();
            while (rs.next()) {
                result.add(mapper.apply(rs));
            }
            return result;
        }
    }

    public static void setDataSource(DataSource dataSource) {
        ApplicationDataSource.dataSource = dataSource;
    }

    public static AutoCloseable beginConnection() throws SQLException {
        var connection = dataSource.getConnection();
        threadConnection.set(connection);
        return () -> {
            threadConnection.get().close();
            threadConnection.remove();
        };
    }
}
