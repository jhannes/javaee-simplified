package com.soprasteria.johannes.simplejava.openid;

import java.net.URI;

public interface IdentityProviderConfig {
    URI getOpenidConfigurationEndpoint();

    String getOpenidClientId();

    String getOpenidSecret();
}
