package com.soprasteria.simplejavaee.api.jaxrs;

import com.soprasteria.generated.simplejavaee.model.UserProfileDto;
import com.soprasteria.simplejavaee.ApplicationConfig;
import com.soprasteria.simplejavaee.ApplicationUserPrincipal;
import jakarta.inject.Inject;
import jakarta.json.Json;
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
import java.net.URL;
import java.util.List;
import java.util.UUID;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

@Path("/login")
public class LoginController {

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
        var userPrincipal = (ApplicationUserPrincipal) securityContext.getUserPrincipal();
        return new UserProfileDto()
                .username(userPrincipal.getName())
                .emailAddress(userPrincipal.getEmail());
    }

    @GET
    @Path("/start")
    public Response startLogin(@Context UriInfo info) throws IOException, URISyntaxException {
        var discoveryDocument = config.getDiscoveryDocumentDto();
        var authorizationState = UUID.randomUUID();
        var queryParameters = List.of(
                "client_id=" + encode(config.getOuathClientId(), UTF_8),
                "redirect_uri=" + encode(info.getBaseUri().resolve("/api/login/callback").toString(), UTF_8),
                "scope=" + encode("openid email", UTF_8),
                "state=" + authorizationState,
                "response_type=code"
        );
        var query = String.join("&", queryParameters);
        var authorizationUri = new URI(discoveryDocument.getString("authorization_endpoint"));
        return Response
                .temporaryRedirect(new URI(authorizationUri + "?" + query))
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

        var formParameters = List.of(
                "client_id=" + encode(config.getOuathClientId(), UTF_8),
                "client_secret=" + encode(config.getOuathClientSecret(), UTF_8),
                "redirect_uri=" + encode(info.getBaseUri().resolve("/api/login/callback").toString(), UTF_8),
                "code=" + encode(code, UTF_8),
                "grant_type=authorization_code"
        );
        var discoveryDocumentDto = config.getDiscoveryDocumentDto();
        var connection = (HttpURLConnection) new URL(discoveryDocumentDto.getString("token_endpoint")).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        connection.getOutputStream().write(String.join("&", formParameters).getBytes());
        if (connection.getResponseCode() >= 300) {
            var response = new ByteArrayOutputStream();
            connection.getErrorStream().transferTo(response);
            throw new IOException("Unsuccessful http request " + connection.getResponseCode() + " " + connection.getResponseMessage() + ": " + response);
        }
        var tokenResponse = Json.createReader(connection.getInputStream()).readObject();
        var accessToken = tokenResponse.getString("access_token");
        return Response.temporaryRedirect(info.getBaseUri().resolve("/"))
                .cookie(new NewCookie.Builder(ACCESS_TOKEN_COOKIE).path("/").value(accessToken).build())
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
}
