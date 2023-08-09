package com.soprasteria.johannes.simplejava.api;

import com.soprasteria.johannes.generated.openid.IdentityProviderApi;
import com.soprasteria.johannes.generated.openid.model.ResponseTypeDto;
import com.soprasteria.johannes.simplejava.ApplicationConfig;
import com.soprasteria.johannes.simplejava.eventsource.generated.model.UserProfileDto;
import com.soprasteria.johannes.simplejava.openid.HttpDiscoveryApi;
import com.soprasteria.johannes.simplejava.openid.HttpIdentityProviderApi;
import jakarta.inject.Inject;
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
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Path("/login")
public class LoginController {

    @Inject
    private ApplicationConfig config;

    @GET
    public UserProfileDto getUserProfile(@CookieParam("accessToken") String accessToken) throws IOException, InterruptedException {
        if (accessToken == null || accessToken.isEmpty()) {
            throw new NotAuthorizedException("Not Authorized");
        }

        var openIdClient = new HttpIdentityProviderApi(config);
        var userinfoResponse = openIdClient.getUserinfo(accessToken);
        return switch (userinfoResponse) {
            case HttpIdentityProviderApi.GetUserinfoSuccess success -> new UserProfileDto().username(success.content().getName());
            case HttpIdentityProviderApi.GetUserinfo401Response unauthorized -> throw new NotAuthorizedException(unauthorized.content());
            case HttpIdentityProviderApi.GetUserinfoErrorResponse error -> throw new ServerErrorException(error.textResponse(), 500);
        };
    }

    @GET
    @Path("/start")
    public Response startLogin(@Context UriInfo info) throws IOException, InterruptedException {
        var api = new HttpDiscoveryApi(config.getOpenidConfigurationEndpoint());
        var discoveryDocument = api.getDiscoveryDocument();
        var authorizationState = UUID.randomUUID().toString();
        var query = new IdentityProviderApi.StartAuthorizationQuery()
                .responseType(ResponseTypeDto.CODE)
                .clientId(config.getOpenidClientId())
                .scope("openid profile email")
                .state(authorizationState)
                .redirectUri(info.getBaseUri().resolve("/api/login/callback"));
        var authorizationUri = UriBuilder.fromUri(discoveryDocument.getAuthorizationEndpoint())
                .replaceQuery(query.toUrlEncoded())
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

        var openIdClient = new HttpIdentityProviderApi(config);
        var tokenResponse = openIdClient.fetchToken(code, redirectUri);

        return switch (tokenResponse) {
            case HttpIdentityProviderApi.FetchTokenSuccess success -> Response
                    .temporaryRedirect(info.getBaseUri().resolve("/"))
                    .cookie(new NewCookie.Builder("authorizationState").maxAge(0).value("").build())
                    .cookie(new NewCookie.Builder("accessToken").value(success.content().getAccessToken()).build())
                    .build();
            case HttpIdentityProviderApi.FetchToken400Response errorResponse -> {
                log.error("An error occurred error={}: {}", errorResponse.content().getError(), errorResponse.content().getErrorDescription());
                throw new ServerErrorException("Error " + errorResponse.content().getError(), 500);
            }
            case HttpIdentityProviderApi.FetchTokenErrorResponse errorResponse -> {
                log.error("An error occurred error={}: {}", errorResponse.statusCode(), errorResponse.textResponse());
                throw new ServerErrorException("Error " + errorResponse.textResponse(), 500);
            }
        };
    }

}
