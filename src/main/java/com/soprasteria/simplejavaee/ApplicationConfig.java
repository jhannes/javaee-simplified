package com.soprasteria.simplejavaee;

import com.soprasteria.infrastructure.Environment;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.flywaydb.core.Flyway;
import org.postgresql.ds.PGSimpleDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.net.HttpURLConnection;
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
        Flyway.configure().dataSource(dataSource).load().migrate();
        return dataSource;
    }

    public JsonObject getDiscoveryDocumentDto() throws IOException {
        var connection = (HttpURLConnection) new URL(getIssuerUrl() + "/.well-known/openid-configuration").openConnection();
        if (connection.getResponseCode() >= 300) {
            throw new IOException("Unsuccessful http request " + connection.getResponseCode() + " " + connection.getResponseMessage());
        }
        return Json.createReader(connection.getInputStream()).readObject();
    }
}
