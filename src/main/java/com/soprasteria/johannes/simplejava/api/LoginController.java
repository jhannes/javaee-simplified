package com.soprasteria.johannes.simplejava.api;

import com.soprasteria.johannes.generated.openid.IdentityProviderApi;
import com.soprasteria.johannes.generated.openid.model.DiscoveryDocumentDto;
import com.soprasteria.johannes.generated.openid.model.ResponseTypeDto;
import com.soprasteria.johannes.generated.openid.model.TokenResponseDto;
import com.soprasteria.johannes.generated.openid.model.UserinfoDto;
import com.soprasteria.johannes.simplejava.ApplicationConfig;
import com.soprasteria.johannes.simplejava.eventsource.generated.model.UserProfileDto;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.ServerErrorException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Slf4j
@Path("/login")
public class LoginController {

    @Inject
    private ApplicationConfig config;
    private final JsonbConfig jsonbConfigForOpenidConfiguration = new JsonbConfig()
            .withDeserializers(new EnumDeserializer())
            .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
    private final Jsonb openidJsonb = JsonbBuilder.newBuilder().withConfig(jsonbConfigForOpenidConfiguration).build();

    @GET
    public UserProfileDto getUserProfile(@CookieParam("accessToken") String accessToken) throws IOException, InterruptedException {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new NotAuthorizedException("Not Authorized");
        }
        var discoveryDocument = getDiscoveryDocumentDto();
        var request = HttpRequest.newBuilder(discoveryDocument.getUserinfoEndpoint())
                .GET().header("Authorization", "Bearer " + accessToken).build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        var userinfo = openidJsonb.fromJson(response.body(), UserinfoDto.class);

        return new UserProfileDto().username(userinfo.get("name").toString());
    }

    @GET
    @Path("/start")
    public Response startLogin(@Context UriInfo info) {
        var discoveryDocument = getDiscoveryDocumentDto();
        var authorizationState = UUID.randomUUID().toString();
        var redirectUri = info.getBaseUri().resolve("/api/login/callback");
        var authorizationUri = UriBuilder.fromUri(discoveryDocument.getAuthorizationEndpoint())
                .queryParam("response_type", ResponseTypeDto.CODE)
                .queryParam("client_id", config.getOpenidClientId())
                .queryParam("state", authorizationState)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", "openid profile email")
                .build();
        return Response
                .temporaryRedirect(authorizationUri)
                .cookie(new NewCookie.Builder("authorizationState").value(authorizationState).build())
                .build();
    }

    @GET
    @Path("/callback")
    public Response callback(
            @Context UriInfo info,
            @QueryParam("code") String code,
            @QueryParam("error") String error,
            @QueryParam("error_description") String errorDescription,
            @QueryParam("state") String state,
            @CookieParam("authorizationState") String cookieState
    ) throws InterruptedException, IOException {
        if (!state.equals(cookieState)) {
            log.info("Invalid authorization state parameter={} cookie={}", state, cookieState);
            throw new ClientErrorException("Invalid authorization state parameter", 400);
        }
        if (error != null) {
            log.error("Error on callback {}: {}", error, errorDescription);
            throw new ServerErrorException(error + ": " + errorDescription, 500);
        } else if (code == null) {
            log.error("Callback without error or code!");
            throw new ServerErrorException("Callback without error or code!", 500);
        }
        var redirectUri = info.getBaseUri().resolve("/api/login/callback");
        var tokenPayload = new IdentityProviderApi.FetchTokenForm()
                .clientId(config.getOpenidClientId())
                .clientSecret(config.getOpenidSecret())
                .code(code)
                .redirectUri(redirectUri)
                .toUrlEncoded();
        var discoveryDocument = getDiscoveryDocumentDto();
        var request = HttpRequest.newBuilder(discoveryDocument.getTokenEndpoint())
                .POST(HttpRequest.BodyPublishers.ofString(tokenPayload))
                .header("Content-Type", "application/x-www-form-urlencoded")
                .build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log.error("An error occurred status={}: {}", response.statusCode(), response.body());
            return Response.serverError()
                    .entity("An error occurred")
                    .build();
        }

        var tokenResponse = openidJsonb.fromJson(response.body(), TokenResponseDto.class);

        return Response
                .temporaryRedirect(info.getBaseUri().resolve("/"))
                .cookie(new NewCookie.Builder("authorizationState").maxAge(0).value("").build())
                .cookie(new NewCookie.Builder("accessToken").value(tokenResponse.getAccessToken()).build())
                .build();
    }

    @SneakyThrows
    private DiscoveryDocumentDto getDiscoveryDocumentDto() {
        var request = HttpRequest.newBuilder(config.getOpenidConfigurationEndpoint()).GET().build();
        var response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofInputStream());
        return openidJsonb.fromJson(response.body(), DiscoveryDocumentDto.class);
    }
}
