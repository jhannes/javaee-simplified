package com.soprasteria.simplejavaee;

import com.soprasteria.generated.openid.model.UserinfoDto;

import java.security.Principal;

class ApplicationUserPrincipal implements Principal {
    private final UserinfoDto userInfo;

    public ApplicationUserPrincipal(UserinfoDto userInfo) {
        this.userInfo = userInfo;
    }

    @Override
    public String getName() {
        return userInfo.getName();
    }
}
