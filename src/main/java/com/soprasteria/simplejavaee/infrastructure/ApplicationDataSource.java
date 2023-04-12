package com.soprasteria.simplejavaee.infrastructure;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationDataSource {

    public static class Transaction implements AutoCloseable {

        private boolean shouldCommit = false;

        private final Connection connection;

        public Transaction(Connection connection) throws SQLException {
            this.connection = connection;
            connection.setAutoCommit(false);
        }

        public void setComplete() {
            this.shouldCommit = true;
        }

        @Override
        public void close() throws SQLException {
            if (shouldCommit) {
                connection.commit();
            } else {
                connection.rollback();
            }
            connection.setAutoCommit(true);
        }
    }


    private static final ThreadLocal<Connection> threadConnection = new ThreadLocal<>();
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

    public static ThrowingClosable<SQLException> beginConnection() throws SQLException {
        // TODO: We want to lazy-initialize getting the connection (but still not dispense connections if not started)
        var connection = dataSource.getConnection();
        threadConnection.set(connection);
        return () -> {
            threadConnection.get().close();
            threadConnection.remove();
        };
    }

    public static Transaction requireTransaction() throws SQLException {
        // TODO: What if there already is a transaction?
        return new Transaction(threadConnection.get());
    }
}
