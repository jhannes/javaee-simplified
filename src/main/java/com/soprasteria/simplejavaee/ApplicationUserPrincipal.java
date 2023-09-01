package com.soprasteria.simplejavaee;

import jakarta.json.JsonObject;

import java.security.Principal;

public class ApplicationUserPrincipal implements Principal {
    private final String name;
    private final String email;

    public ApplicationUserPrincipal(JsonObject userInfo) {
        name = userInfo.getString("name");
        email = userInfo.getString("email");
    }

    @Override
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}
