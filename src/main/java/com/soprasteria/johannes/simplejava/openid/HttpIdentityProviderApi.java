package com.soprasteria.johannes.simplejava.openid;

import com.soprasteria.johannes.generated.openid.IdentityProviderApi;
import com.soprasteria.johannes.generated.openid.model.DiscoveryDocumentDto;
import com.soprasteria.johannes.generated.openid.model.GrantTypeDto;
import com.soprasteria.johannes.generated.openid.model.OauthErrorDto;
import com.soprasteria.johannes.generated.openid.model.TokenResponseDto;
import com.soprasteria.johannes.generated.openid.model.UserinfoDto;
import com.soprasteria.johannes.simplejava.ApplicationConfig;
import jakarta.json.JsonStructure;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.PropertyNamingStrategy;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Slf4j
public class HttpIdentityProviderApi implements ApiClient {
    private static final JsonbConfig jsonbConfigForOpenidConfiguration = new JsonbConfig()
            .withDeserializers(new EnumDeserializer())
            .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
    public static final Jsonb openidJsonb = JsonbBuilder.newBuilder()
            .withConfig(jsonbConfigForOpenidConfiguration)
            .build();


    private final DiscoveryDocumentDto discoveryDocument;
    private final IdentityProviderConfig config;
    private final Jsonb jsonb;

    public HttpIdentityProviderApi(IdentityProviderConfig config, Jsonb jsonb) throws IOException, InterruptedException {
        this.config = config;
        this.jsonb = jsonb;
        var api = new HttpDiscoveryApi(config.getOpenidConfigurationEndpoint(), jsonb);
        this.discoveryDocument = api.getDiscoveryDocument();
    }

    public HttpIdentityProviderApi(ApplicationConfig config) throws IOException, InterruptedException {
        this(config, openidJsonb);
    }

    sealed public interface GetUserinfoResponse {
    }

    public record GetUserinfoSuccess(UserinfoDto content) implements GetUserinfoResponse {
    }

    public record GetUserinfo401Response(String content) implements GetUserinfoResponse {
    }

    public sealed interface GetUserinfoErrorResponse extends GetUserinfoResponse, ApiClient.ErrorResponse {}

    public record GetUserinfoUnexpectedError(int statusCode, String content) implements GetUserinfoErrorResponse, ApiClient.ErrorTextResponse {
    }

    public GetUserinfoResponse getUserinfo(String accessToken) throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(discoveryDocument.getUserinfoEndpoint())
                .GET().header("Authorization", "Bearer " + accessToken).build();
        return handleGetUserinfoResponse(sendRequest(request));
    }

    public GetUserinfoResponse handleGetUserinfoResponse(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            return new GetUserinfoSuccess(jsonb.fromJson(response.body(), UserinfoDto.class));
        } else if (response.statusCode() == 401) {
            return new GetUserinfo401Response(response.body());
        } else {
            log.error("statusCode={} body={}", response.statusCode(), response.body());
            return new GetUserinfoUnexpectedError(response.statusCode(), response.body());
        }
    }

    public sealed interface FetchTokenResponse {
    }

    public record FetchTokenSuccess(TokenResponseDto content) implements FetchTokenResponse {
    }

    public record FetchToken400Response(OauthErrorDto content) implements FetchTokenResponse {
    }

    public sealed interface FetchTokenErrorResponse extends FetchTokenResponse, ApiClient.ErrorResponse {}

    public record FetchTokenJsonError(int statusCode, JsonStructure content) implements FetchTokenErrorResponse, ApiClient.ErrorJsonResponse {
    }

    public record FetchTokenUnexpectedError(int statusCode, String content) implements FetchTokenErrorResponse, ApiClient.ErrorTextResponse {
    }

    public FetchTokenResponse fetchToken(String code, URI redirectUri) throws IOException, InterruptedException {
        var tokenPayload = new IdentityProviderApi.FetchTokenForm()
                .grantType(GrantTypeDto.AUTHORIZATION_CODE)
                .clientId(config.getOpenidClientId())
                .clientSecret(config.getOpenidSecret())
                .code(code)
                .redirectUri(redirectUri)
                .toUrlEncoded();
        var request = HttpRequest.newBuilder(discoveryDocument.getTokenEndpoint())
                .POST(HttpRequest.BodyPublishers.ofString(tokenPayload))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        var response = sendRequest(request);
        return handleFetchTokenResponse(response);
    }

    public FetchTokenResponse handleFetchTokenResponse(HttpResponse<String> response) {
        if (response.statusCode() == 200) {
            return new FetchTokenSuccess(jsonb.fromJson(response.body(), TokenResponseDto.class));
        } else if (response.statusCode() == 400 && isJsonResponse(response)) {
            return new FetchToken400Response(jsonb.fromJson(response.body(), OauthErrorDto.class));
        } else if (isJsonResponse(response)) {
            return new FetchTokenJsonError(response.statusCode(), jsonb.fromJson(response.body(), JsonStructure.class));
        } else {
            return new FetchTokenUnexpectedError(response.statusCode(), response.body());
        }
    }
}
