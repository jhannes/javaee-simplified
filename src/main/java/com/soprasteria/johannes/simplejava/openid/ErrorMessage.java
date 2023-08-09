package com.soprasteria.johannes.simplejava.openid;

import jakarta.json.JsonObject;

public record ErrorMessage(String code, String message, JsonObject innerError) {
}
