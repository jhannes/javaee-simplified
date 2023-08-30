package com.soprasteria.simplejavaee.api;

import com.soprasteria.generated.openid.api.HttpDiscoveryApi;
import com.soprasteria.generated.openid.api.IdentityProviderApi;
import com.soprasteria.generated.openid.model.DiscoveryDocumentDto;
import com.soprasteria.generated.openid.model.GrantTypeDto;
import com.soprasteria.generated.openid.model.ResponseTypeDto;
import com.soprasteria.generated.openid.model.TokenResponseDto;
import com.soprasteria.generated.simplejavaee.model.UserProfileDto;
import com.soprasteria.infrastructure.JsonbEnumDeserializer;
import com.soprasteria.simplejavaee.ApplicationConfig;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.CookieParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import jakarta.ws.rs.core.UriInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Path("/login")
public class LoginController {

    public static final Jsonb openidJsonb = JsonbBuilder.create(new JsonbConfig()
            .withDeserializers(new JsonbEnumDeserializer())
            .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES)
    );
    public static final String ACCESS_TOKEN_COOKIE = "accessToken";
    @Inject
    private ApplicationConfig config;

    @Context
    private SecurityContext securityContext;

    @GET
    public UserProfileDto getUserProfile() {
        if (securityContext == null || securityContext.getUserPrincipal() == null) {
            throw new ClientErrorException(Response.Status.UNAUTHORIZED);
        }
        return new UserProfileDto().username(securityContext.getUserPrincipal().getName());
    }

    @GET
    @Path("/start")
    public Response startLogin(@Context UriInfo info) throws IOException, URISyntaxException {
        var discoveryDocument = getDiscoveryDocumentDto();
        var authorizationState = UUID.randomUUID();

        var query = new IdentityProviderApi.StartAuthorizationQuery()
                .clientId(config.getOuathClientId())
                .redirectUri(info.getBaseUri().resolve("/api/login/callback"))
                .scope("openid email")
                .state(authorizationState.toString())
                .responseType(ResponseTypeDto.CODE)
                .toUrlEncoded();
        var authorizationUri = new URI(discoveryDocument.getAuthorizationEndpoint() + "?" + query);

        return Response.temporaryRedirect(authorizationUri)
                .cookie(new NewCookie.Builder("authorizationState").value(authorizationState.toString()).build())
                .build();
    }

    @GET
    @Path("/callback")
    public Response callback(
            @Context UriInfo info,
            @QueryParam("code") String code, @QueryParam("state") String state, @QueryParam("error") String error,
            @CookieParam("authorizationState") String expectedState
    ) throws IOException {
        if (!state.equals(expectedState)) {
            throw new ClientErrorException("Invalid state", 400);
        }

        var discoveryDocumentDto = getDiscoveryDocumentDto();
        var connection = (HttpURLConnection) discoveryDocumentDto.getTokenEndpoint().toURL().openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        connection.getOutputStream().write(new IdentityProviderApi.FetchTokenForm()
                .clientId(config.getOuathClientId())
                .redirectUri(info.getBaseUri().resolve("/api/login/callback"))
                .clientSecret(config.getOuathClientSecret())
                .code(code)
                .grantType(GrantTypeDto.AUTHORIZATION_CODE)
                .toUrlEncoded().getBytes()
        );
        if (connection.getResponseCode() >= 300) {
            var response = new ByteArrayOutputStream();
            connection.getErrorStream().transferTo(response);
            throw new IOException("Unsuccessful http request " + connection.getResponseCode() + " " + connection.getResponseMessage() + ": " + response);
        }

        var tokenResponse = openidJsonb.fromJson(connection.getInputStream(), TokenResponseDto.class);

        return Response.temporaryRedirect(info.getBaseUri().resolve("/"))
                .cookie(new NewCookie.Builder(ACCESS_TOKEN_COOKIE).path("/").value(tokenResponse.getAccessToken()).build())
                .cookie(new NewCookie.Builder("authorizationState").maxAge(0).value("").build())
                .build();
    }

    @GET
    @Path("/endsession")
    public Response callback(@Context UriInfo info) {
        return Response.temporaryRedirect(info.getBaseUri().resolve("/"))
                .cookie(new NewCookie.Builder(ACCESS_TOKEN_COOKIE).path("/").maxAge(0).value("").build())
                .build();
    }


    private DiscoveryDocumentDto getDiscoveryDocumentDto() throws IOException {
        var discoveryApi = new HttpDiscoveryApi(config.getIssuerUrl(), openidJsonb);
        return discoveryApi.getDiscoveryDocument();
    }

}
