package fr.huiitre.tools.infrastructure.auth.google;

public class GoogleUserPayload {

    private final String providerUserId; // sub
    private final String email;
    private final String name;

    public GoogleUserPayload(String providerUserId, String email, String name) {
        this.providerUserId = providerUserId;
        this.email = email;
        this.name = name;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }
}
