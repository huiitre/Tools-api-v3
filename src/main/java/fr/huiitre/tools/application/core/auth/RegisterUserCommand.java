package fr.huiitre.tools.application.core.auth;

import fr.huiitre.tools.application.core.auth.AuthProvider;

public class RegisterUserCommand {

    private final AuthProvider provider;

    // Identité commune
    private final String email;
    private final String name;

    // PASSWORD only
    private final String password;

    // OAUTH only
    private final String providerUserId;

    private RegisterUserCommand(
            AuthProvider provider,
            String email,
            String name,
            String password,
            String providerUserId) {

        this.provider = provider;
        this.email = email;
        this.name = name;
        this.password = password;
        this.providerUserId = providerUserId;
    }

    /* =========================
     * FACTORY METHODS
     * ========================= */

    public static RegisterUserCommand password(
            String email,
            String name,
            String password) {

        return new RegisterUserCommand(
                AuthProvider.PASSWORD,
                email,
                name,
                password,
                null
        );
    }

    public static RegisterUserCommand oauth(
            AuthProvider provider,
            String providerUserId,
            String email,
            String name) {

        if (provider == AuthProvider.PASSWORD) {
            throw new IllegalArgumentException("PASSWORD provider not allowed here");
        }

        return new RegisterUserCommand(
                provider,
                email,
                name,
                null,
                providerUserId
        );
    }

    /* =========================
     * GETTERS
     * ========================= */

    public AuthProvider getProvider() {
        return provider;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getProviderUserId() {
        return providerUserId;
    }

    /* =========================
     * HELPERS
     * ========================= */

    public boolean isPasswordAuth() {
        return provider == AuthProvider.PASSWORD;
    }

    public boolean isOAuthAuth() {
        return provider != AuthProvider.PASSWORD;
    }
}
