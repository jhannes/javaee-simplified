package com.soprasteria.johannes.simplejava;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationServerTest {

    @Test
    void shouldStartOnSpecifiedPort() throws IOException, InterruptedException {
        var server = new ApplicationServer(0);
        server.start();

        var request = HttpRequest.newBuilder(server.getURI()).GET().build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
    }

}