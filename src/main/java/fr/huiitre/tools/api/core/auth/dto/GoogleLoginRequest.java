package fr.huiitre.tools.api.core.auth.dto;

import jakarta.validation.constraints.NotBlank;

public class GoogleLoginRequest {

    @NotBlank
    private String idToken;

    public String getIdToken() {
        return idToken;
    }
}
