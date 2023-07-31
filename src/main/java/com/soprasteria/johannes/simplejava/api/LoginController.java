package com.soprasteria.johannes.simplejava.api;

import com.soprasteria.johannes.simplejava.eventsource.generated.model.UserProfileDto;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/login")
public class LoginController {

    @GET
    public UserProfileDto getUserProfile() {
        throw new RuntimeException();
    }
}
