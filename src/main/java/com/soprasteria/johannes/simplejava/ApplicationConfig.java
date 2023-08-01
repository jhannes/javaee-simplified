package com.soprasteria.johannes.simplejava;

import com.soprasteria.johannes.infrastructure.Environment;

import java.net.URI;

public class ApplicationConfig {

    private final Environment env;

    public ApplicationConfig(Environment env) {
        this.env = env;
    }

    public URI getOpenidConfigurationEndpoint() {
        return env.getURI(
                "OPENID_CONFIGURATION_ENDPOINT",
                "https://login.microsoftonline.com/common/v2.0/.well-known/openid-configuration"
        );
    }

    public String getOpenidClientId() {
        return env.get("OPENID_CLIENT_ID");
    }

    public String getOpenidSecret() {
        return env.get("OPENID_CLIENT_SECRET");
    }
}
