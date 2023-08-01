package com.soprasteria.johannes.simplejava.api;

import com.soprasteria.johannes.simplejava.ApplicationServer;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class LoginControllerTest {

    private final ApplicationServer server = ApplicationServer.start(0);

    @Test
    void shouldRejectUnauthorizedUser() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(server.getURI().resolve("/api/login")).GET().build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        assertThat(response.statusCode()).isEqualTo(401);
    }

}