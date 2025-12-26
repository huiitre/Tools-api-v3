package fr.huiitre.tools.application.core.auth.login;

import org.springframework.stereotype.Service;

import fr.huiitre.tools.application.core.auth.Authprovider;
import fr.huiitre.tools.application.core.auth.PasswordHasher;
import fr.huiitre.tools.application.core.auth.exception.InvalidCredentialsException;
import fr.huiitre.tools.application.core.auth.exception.UserDisabledException;
import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.application.core.user.ports.UserCredentialsRepository;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.domain.core.user.User;

@Service
public class LoginUserUseCase {

    private final UserRepository userRepository;
    private final UserCredentialsRepository credentialsRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final PasswordHasher passwordHasher;

    public LoginUserUseCase(
        UserRepository userRepository,
        UserCredentialsRepository credentialsRepository,
        UserAuthProviderRepository userAuthProviderRepository,
        PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.credentialsRepository = credentialsRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(LoginUserCommand command) {

        /*
         * Charger l’utilisateur à partir de l’email.
         *    - L’email est l’identifiant fonctionnel.
         *    - Si l’utilisateur n’existe pas → échec login.
         *    - Message volontairement générique (pas d’info leak).
         */
        User user = userRepository
            .findByEmail(command.getEmail())
            .orElseThrow(() ->
                new InvalidCredentialsException()
            );

        /*
         * Utilisateur désactivé 
         */
        if (!user.isActive()) {
            throw new UserDisabledException("Utilisateur désactivé");
        }

        /*
         * Vérification du PROVIDER de l'utilisateur
         */
        boolean hasPasswordProvider =
            userAuthProviderRepository.existsByUserIdAndProvider(
                user.getId(),
                Authprovider.PASSWORD
            );

        if (!hasPasswordProvider) {
            throw new InvalidCredentialsException();
        }

        /*
         * Récupérer le hash du mot de passe.
         *    - Séparé de User (table user_credentials).
         *    - Un user peut exister sans credentials (OAuth plus tard).
         */
        String passwordHash = credentialsRepository
            .findPasswordHashByUserId(user.getId())
            .orElseThrow(() ->
                new InvalidCredentialsException()
            );

        /*
         * Vérifier le mot de passe.
         *    - Le use case ne connaît pas bcrypt.
         *    - Il délègue au port PasswordHasher.
         */
        boolean valid = passwordHasher.matches(
            command.getPassword(),
            passwordHash
        );

        if (!valid) {
            throw new InvalidCredentialsException();
        }

        /*
         * Login réussi.
         *    - Le use case retourne l’identité authentifiée.
         *    - Pas de JWT ici.
         *    - Pas de HTTP ici.
         */
        return user;
    }
}
