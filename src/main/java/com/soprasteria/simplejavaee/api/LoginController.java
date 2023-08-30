package com.soprasteria.simplejavaee.api;

import com.soprasteria.generated.openid.api.HttpDiscoveryApi;
import com.soprasteria.generated.openid.api.IdentityProviderApi;
import com.soprasteria.generated.openid.model.ResponseTypeDto;
import com.soprasteria.generated.simplejavaee.model.UserProfileDto;
import com.soprasteria.simplejavaee.ApplicationConfig;
import jakarta.inject.Inject;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.UUID;

@Path("/login")
public class LoginController {

    @Inject
    private ApplicationConfig config;

    @GET
    public UserProfileDto getUserProfile() {
        throw new ClientErrorException(Response.Status.UNAUTHORIZED);
    }

    @GET
    @Path("/start")
    public Response startLogin(@Context UriInfo info) throws IOException, URISyntaxException {
        var discoveryApi = new HttpDiscoveryApi(config.getOpenIdDiscoveryUrl());
        var discoveryDocument = discoveryApi.getDiscoveryDocument();
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
}
