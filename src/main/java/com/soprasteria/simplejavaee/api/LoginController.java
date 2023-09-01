package com.soprasteria.simplejavaee.api;

import com.soprasteria.generated.simplejavaee.model.UserProfileDto;
import com.soprasteria.simplejavaee.ApplicationConfig;
import com.soprasteria.simplejavaee.ApplicationUserPrincipal;
import org.actioncontroller.actions.GET;
import org.actioncontroller.exceptions.HttpRequestException;
import org.actioncontroller.optional.json.Json;
import org.actioncontroller.values.ContextUrl;
import org.actioncontroller.values.RequestParam;
import org.actioncontroller.values.SendRedirect;
import org.actioncontroller.values.UnencryptedCookie;
import org.actioncontroller.values.UserPrincipal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static java.net.URLEncoder.encode;
import static java.nio.charset.StandardCharsets.UTF_8;

public class LoginController {

    private final ApplicationConfig config;

    public LoginController(ApplicationConfig config) {
        this.config = config;
    }

    @GET("/login")
    @Json
    public UserProfileDto getUserProfile(@UserPrincipal ApplicationUserPrincipal principal) {
        return new UserProfileDto()
                .username(principal.getName())
                .emailAddress(principal.getEmail());
    }

    @GET("/login/start")
    @SendRedirect
    public String startLogin(
            @ContextUrl URL baseUrl,
            @UnencryptedCookie("expectedState") Consumer<String> setCookie
    ) throws IOException, URISyntaxException {
        var discoveryDocument = config.getDiscoveryDocumentDto();
        var authorizationEndpoint = new URI(discoveryDocument.getString("authorization_endpoint"));

        var authorizationState = UUID.randomUUID().toString();
        setCookie.accept(authorizationState);
        var queryParameters = List.of(
                "client_id=" + encode(config.getOuathClientId(), UTF_8),
                "redirect_uri=" + encode(new URL(baseUrl, "/api/login/callback").toString(), UTF_8),
                "scope=" + encode("openid email", UTF_8),
                "state=" + authorizationState,
                "response_type=code"
        );
        return authorizationEndpoint + "?" + String.join("&", queryParameters);
    }

    @GET("/login/callback?code")
    @SendRedirect("/")
    public void handleCallback(
            @RequestParam("state") String state,
            @RequestParam("code") String code,
            @ContextUrl URL baseUrl,
            @UnencryptedCookie("expectedState") String expectedState,
            @UnencryptedCookie("accessToken") Consumer<String> setAccessToken
    ) throws IOException {
        if (!expectedState.equals(state)) {
            throw new HttpRequestException("Unexpected state");
        }

        var formParameters = List.of(
                "client_id=" + encode(config.getOuathClientId(), UTF_8),
                "client_secret=" + encode(config.getOuathClientSecret(), UTF_8),
                "redirect_uri=" + encode(new URL(baseUrl, "/api/login/callback").toString(), UTF_8),
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
        var tokenResponse = jakarta.json.Json.createReader(connection.getInputStream()).readObject();
        var accessToken = tokenResponse.getString("access_token");
        setAccessToken.accept(accessToken);
    }

    @GET("/login/endsession")
    @SendRedirect("/")
    public void endSession(@UnencryptedCookie("accessToken") Consumer<String> setAccessToken) {
        setAccessToken.accept(null);
    }
}
