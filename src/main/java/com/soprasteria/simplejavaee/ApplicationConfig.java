package com.soprasteria.simplejavaee;

import com.soprasteria.infrastructure.Environment;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.net.MalformedURLException;
import java.net.URL;

public class ApplicationConfig {

    private final Environment environment = new Environment();

    public URL getIssuerUrl() throws MalformedURLException {
        return new URL(environment.get("OPENID_ISSUER_URI", "https://login.microsoftonline.com/common/v2.0/"));
    }

    public String getOuathClientId() {
        return environment.get("OAUTH_CLIENT_ID");
    }

    public String getOuathClientSecret() {
        return environment.get("OAUTH_CLIENT_SECRET");
    }

    public DataSource createDataSource() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setURL(environment.get("JDBC_URL", "jdbc:postgresql://localhost:5432/postgres"));
        dataSource.setUser(environment.get("JDBC_USER", "postgres"));
        dataSource.setPassword(environment.get("JDBC_PASSWORD", "postgres"));
        return dataSource;
    }
}
