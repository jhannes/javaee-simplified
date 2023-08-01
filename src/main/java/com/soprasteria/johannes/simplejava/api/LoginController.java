package com.soprasteria.johannes.simplejava.api;

import com.soprasteria.johannes.generated.openid.model.DiscoveryDocumentDto;
import com.soprasteria.johannes.simplejava.ApplicationConfig;
import com.soprasteria.johannes.simplejava.eventsource.generated.model.UserProfileDto;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.PropertyNamingStrategy;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotAuthorizedException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@Path("/login")
public class LoginController {

    @Inject
    private ApplicationConfig config;
    private final JsonbConfig jsonbConfigForOpenidConfiguration = new JsonbConfig()
            .withDeserializers(new EnumDeserializer())
            .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
    private final Jsonb openidJsonb = JsonbBuilder.newBuilder().withConfig(jsonbConfigForOpenidConfiguration).build();

    @GET
    public UserProfileDto getUserProfile() {
        throw new NotAuthorizedException("Not Authorized");
    }

    @GET
    @Path("/start")
    public Response startLogin(@Context UriInfo info) throws InterruptedException, IOException {
        var discoveryDocument = getDiscoveryDocumentDto();
        var authorizationState = UUID.randomUUID().toString();
        var redirectUri = info.getBaseUri().resolve("/api/login/callback");
        var authorizationUri = UriBuilder.fromUri(discoveryDocument.getAuthorizationEndpoint())
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

    private DiscoveryDocumentDto getDiscoveryDocumentDto() throws IOException, InterruptedException {
        var request = HttpRequest.newBuilder(config.getOpenidConfigurationEndpoint()).GET().build();
        var response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofInputStream());
        return openidJsonb.fromJson(response.body(), DiscoveryDocumentDto.class);
    }
}
