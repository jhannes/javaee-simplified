package com.soprasteria.johannes.simplejava.openid;

import jakarta.json.JsonStructure;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public interface ApiClient {
    interface ErrorResponse {
        int statusCode();
        String textResponse();
    }

    interface ErrorJsonResponse extends ErrorResponse {
        JsonStructure content();
        default String textResponse() {
            return content().toString();
        }
    }

    interface ErrorTextResponse extends ErrorResponse {
        String content();
        default String textResponse() {
            return content();
        }
    }

    default HttpResponse<String> sendRequest(HttpRequest request) throws IOException, InterruptedException {
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
    }

    default boolean isJsonResponse(HttpResponse<String> response) {
        return response.headers().firstValue("content-type").filter(s -> s.startsWith("application/json")).isPresent();
    }
}
