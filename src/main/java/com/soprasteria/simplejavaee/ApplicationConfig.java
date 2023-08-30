package com.soprasteria.simplejavaee;

import com.soprasteria.infrastructure.Environment;

import java.net.MalformedURLException;
import java.net.URL;

public class ApplicationConfig {

    private final Environment environment = new Environment();

    public String getOuathClientId() {
        return environment.get("OAUTH_CLIENT_ID");
    }

    public URL getOpenIdDiscoveryUrl() throws MalformedURLException {
        return new URL(environment.get("OPENID_DISCOVERY_URI", "https://login.microsoftonline.com/common/v2.0/.well-known"));
    }
}
