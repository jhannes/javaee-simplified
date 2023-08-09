package com.soprasteria.johannes.simplejava;

import com.soprasteria.johannes.infrastructure.Environment;
import com.soprasteria.johannes.simplejava.openid.IdentityProviderConfig;

import java.net.URI;

public class ApplicationConfig implements IdentityProviderConfig {

    private final Environment env;

    public ApplicationConfig(Environment env) {
        this.env = env;
    }

    @Override
    public URI getOpenidConfigurationEndpoint() {
        return env.getURI(
                "OPENID_CONFIGURATION_ENDPOINT",
                "https://login.microsoftonline.com/common/v2.0/"
        );
    }

    @Override
    public String getOpenidClientId() {
        return env.get("OPENID_CLIENT_ID");
    }

    @Override
    public String getOpenidSecret() {
        return env.get("OPENID_CLIENT_SECRET");
    }
}
