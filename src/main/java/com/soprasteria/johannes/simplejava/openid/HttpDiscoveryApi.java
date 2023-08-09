package com.soprasteria.johannes.simplejava.openid;

import com.soprasteria.johannes.generated.openid.DiscoveryApi;
import com.soprasteria.johannes.generated.openid.model.DiscoveryDocumentDto;
import com.soprasteria.johannes.generated.openid.model.JwksDocumentDto;
import jakarta.json.JsonStructure;
import jakarta.json.bind.Jsonb;
import jakarta.ws.rs.ServerErrorException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpDiscoveryApi implements DiscoveryApi, ApiClient {

    private final Jsonb jsonb;
    private final URI baseUri;


    public HttpDiscoveryApi(URI baseUri) {
        this(baseUri, HttpIdentityProviderApi.openidJsonb);
    }

    public HttpDiscoveryApi(URI baseUri, Jsonb jsonb) {
        this.baseUri = baseUri;
        this.jsonb = jsonb;
    }

    public sealed interface GetDiscoveryDocumentResponse {
    }

    public record GetDiscoveryDocumentSuccess(DiscoveryDocumentDto content) implements GetDiscoveryDocumentResponse {
    }

    public sealed interface GetDiscoveryDocumentErrorResponse extends GetDiscoveryDocumentResponse, ApiClient.ErrorResponse {}

    public record GetDiscoveryDocumentJsonError(int statusCode, JsonStructure content) implements GetDiscoveryDocumentErrorResponse, ApiClient.ErrorJsonResponse {
    }

    public record GetDiscoveryDocumentUnexpectedError(int statusCode, String content) implements GetDiscoveryDocumentErrorResponse, ApiClient.ErrorTextResponse {
    }

    @Override
    public DiscoveryDocumentDto getDiscoveryDocument() throws IOException, InterruptedException {
        var response = getDiscoveryDocumentResponse();
        return switch (response) {
            case GetDiscoveryDocumentSuccess success -> success.content();
            case GetDiscoveryDocumentErrorResponse error ->
                throw new ServerErrorException("Error " + error.statusCode() + ": " + error.textResponse(), 500);
        };
    }

    public GetDiscoveryDocumentResponse getDiscoveryDocumentResponse() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(baseUri.resolve(".well-known/openid-configuration")).GET().build();
        return handleGetDiscoveryDocumentResponse(sendRequest(request));
    }

    private GetDiscoveryDocumentResponse handleGetDiscoveryDocumentResponse(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            return new GetDiscoveryDocumentSuccess(jsonb.fromJson(response.body(), DiscoveryDocumentDto.class));
        } else if (isJsonResponse(response)) {
            return new GetDiscoveryDocumentJsonError(response.statusCode(), jsonb.fromJson(response.body(), JsonStructure.class));
        } else {
            return new GetDiscoveryDocumentUnexpectedError(response.statusCode(), response.body());
        }
    }

    public sealed interface GetJwksDocumentResponse {
    }

    public record GetJwksDocumentSuccess(JwksDocumentDto content) implements GetJwksDocumentResponse {
    }

    public sealed interface GetJwksDocumentErrorResponse extends GetJwksDocumentResponse, ApiClient.ErrorResponse {}

    public record GetJwksDocumentJsonError(int statusCode, JsonStructure content) implements GetJwksDocumentErrorResponse, ApiClient.ErrorJsonResponse {
    }

    public record GetJwksDocumentUnexpectedError(int statusCode, String content) implements GetJwksDocumentErrorResponse, ApiClient.ErrorTextResponse {
    }

    @Override
    public JwksDocumentDto getJwksDocument() throws IOException, InterruptedException {
        var response = getJwksDocumentResponse();
        return switch (response) {
            case GetJwksDocumentSuccess success -> success.content();
            case GetJwksDocumentErrorResponse error ->
                    throw new ServerErrorException("Error " + error.statusCode() + ": " + error.textResponse(), 500);
        };
    }

    public GetJwksDocumentResponse getJwksDocumentResponse() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(baseUri.resolve(".well-known/keys")).GET().build();
        return handleGetJwksDocumentResponse(sendRequest(request));
    }

    private GetJwksDocumentResponse handleGetJwksDocumentResponse(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            return new GetJwksDocumentSuccess(jsonb.fromJson(response.body(), JwksDocumentDto.class));
        } else if (isJsonResponse(response)) {
            return new GetJwksDocumentJsonError(response.statusCode(), jsonb.fromJson(response.body(), JsonStructure.class));
        } else {
            return new GetJwksDocumentUnexpectedError(response.statusCode(), response.body());
        }
    }

}