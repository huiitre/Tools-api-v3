package fr.huiitre.tools.api.core.auth.dto;

public class LoginResponse {

    private final String accessToken;
    private final String tokenType;

    public LoginResponse(String accessToken) {
        this.accessToken = accessToken;
        this.tokenType = "Bearer";
    }

    public String getAccessToken() {
      return accessToken;
    }

    public String getTokenType() {
      return tokenType;
    }
}
