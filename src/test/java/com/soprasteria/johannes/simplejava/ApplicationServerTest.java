package com.soprasteria.johannes.simplejava;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationServerTest {

    private final ApplicationServer server = ApplicationServer.start(0);

    @Test
    void shouldStartOnSpecifiedPort() throws Exception {
        var request = HttpRequest.newBuilder(server.getURI()).GET().build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.headers().firstValue("content-type")).get().isEqualTo("text/html");
        assertThat(response.body()).contains("<title>JavaZone TODO app</title>");
    }
}