package fr.huiitre.tools.application.auth;

import fr.huiitre.tools.application.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.application.user.ports.UserCredentialsRepository;
import fr.huiitre.tools.application.user.ports.UserRepository;
import fr.huiitre.tools.domain.user.User;
import fr.huiitre.tools.domain.user.UserType;
import fr.huiitre.tools.application.auth.exception.RegisterException;
import jakarta.transaction.Transactional;

@Transactional
public class RegisterUserUseCase {

    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(
        UserRepository userRepository,
        UserCredentialsRepository userCredentialsRepository,
        UserAuthProviderRepository userAuthProviderRepository,
        PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(RegisterUserCommand command) {

        if (command.getProvider() != Authprovider.PASSWORD) {
            throw new RegisterException("UNSUPPORTED_AUTH_PROVIDER");
        }

        if (command.getName() == null || command.getName().isBlank()) {
            throw new RegisterException("NAME_REQUIRED");
        }

        boolean alreadyExists =
            userAuthProviderRepository.existsByProviderAndProviderUserId(
                Authprovider.PASSWORD,
                command.getEmail()
            );

        if (alreadyExists) {
            throw new RegisterException("EMAIL_ALREADY_REGISTERED");
        }

        User user = new User(
            command.getName(),
            command.getEmail(),
            UserType.HUMAN
        );

        userRepository.save(user);

        String passwordHash = passwordHasher.hash(command.getPassword());

        userCredentialsRepository.save(
            user.getId(),
            passwordHash
        );

        userAuthProviderRepository.save(
            user.getId(),
            Authprovider.PASSWORD,
            command.getEmail(), // provider_user_id (PASSWORD = email)
            command.getEmail()  // provider_email
        );

        return user;
    }
}
