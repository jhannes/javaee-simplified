package com.soprasteria.johannes.simplejava.api;

import com.soprasteria.johannes.infrastructure.Environment;
import com.soprasteria.johannes.simplejava.ApplicationServer;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private final HashMap<String, String> environment = new HashMap<>();
    private final ApplicationServer server = ApplicationServer.start(0, new Environment(environment));
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Test
    void shouldRejectUnauthorizedUser() throws IOException, InterruptedException {
        var response = httpClient.send(getRequest("/api/login"), BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(401);
    }

    @Test
    void shouldRedirectToLogin() throws IOException, InterruptedException {
        var clientId = UUID.randomUUID().toString();
        environment.put("OPENID_CLIENT_ID", clientId);

        var response = httpClient.send(getRequest("/api/login/start"), BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(307);
        assertThat(response.headers().firstValue("Set-Cookie")).isPresent();

        assertThat(response.headers().firstValue("Location").map(LoginControllerTest::asURI))
                .hasValueSatisfying(uri -> assertThat(uri)
                        .hasHost("login.microsoftonline.com")
                        .hasParameter("state")
                        .hasParameter("client_id", clientId)
                        .hasParameter("scope")
                        .hasParameter("redirect_uri"));
    }

    @SneakyThrows(URISyntaxException.class)
    private static URI asURI(String s) {
        return new URI(s);
    }


    private HttpRequest getRequest(String path) {
        return HttpRequest.newBuilder(server.getURI().resolve(path)).GET().build();
    }

}