package fr.huiitre.tools.application.auth;

public class RegisterUserCommand {

    private final Authprovider provider;
    private final String email;
    private final String password;
    private final String name;

    public RegisterUserCommand(
        Authprovider provider,
        String email,
        String password,
        String name
    ) {
        this.provider = provider;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public Authprovider getProvider() {
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
