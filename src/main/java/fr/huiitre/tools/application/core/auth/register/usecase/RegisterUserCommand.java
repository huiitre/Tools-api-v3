package fr.huiitre.tools.application.core.auth.register.usecase;

import fr.huiitre.tools.application.core.auth.AuthProvider;

public class RegisterUserCommand {

    private final AuthProvider provider;
    private final String email;
    private final String password;
    private final String name;

    public RegisterUserCommand(
        AuthProvider provider,
        String email,
        String password,
        String name
    ) {
        this.provider = provider;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public AuthProvider getProvider() {
        return provider;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }
}
