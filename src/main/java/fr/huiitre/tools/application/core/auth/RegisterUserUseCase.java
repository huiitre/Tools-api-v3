package fr.huiitre.tools.application.core.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.core.auth.AuthProvider;
import fr.huiitre.tools.application.core.auth.PasswordHasher;
import fr.huiitre.tools.application.core.auth.exception.RegisterException;
import fr.huiitre.tools.application.core.auth.RegisterUserCommand;
import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;
import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.application.core.user.ports.UserCredentialsRepository;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.domain.core.role.Role;
import fr.huiitre.tools.domain.core.role.UserRole;
import fr.huiitre.tools.domain.core.user.User;
import fr.huiitre.tools.domain.core.user.UserType;

@Service
@Transactional
public class RegisterUserUseCase {

    private static final Logger logger = LoggerFactory.getLogger(RegisterUserUseCase.class);

    private final UserRepository userRepository;
    private final UserCredentialsRepository userCredentialsRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final PasswordHasher passwordHasher;

    public RegisterUserUseCase(
            UserRepository userRepository,
            UserCredentialsRepository userCredentialsRepository,
            UserAuthProviderRepository userAuthProviderRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository,
            PasswordHasher passwordHasher) {
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(RegisterUserCommand command) {

        /*
         * ===============================
         * VALIDATIONS COMMUNES
         * ===============================
         */
        if (command.getName() == null || command.getName().isBlank()) {
            throw new RegisterException("NAME_REQUIRED");
        }

        if (command.getEmail() == null || command.getEmail().isBlank()) {
            throw new RegisterException("EMAIL_REQUIRED");
        }

        /*
         * ===============================
         * VALIDATIONS PAR PROVIDER
         * ===============================
         */
        if (command.isPasswordAuth()) {

            if (command.getPassword() == null || command.getPassword().isBlank()) {
                throw new RegisterException("PASSWORD_REQUIRED");
            }

            boolean alreadyExists = userAuthProviderRepository
                    .existsByProviderAndProviderUserId(
                            AuthProvider.PASSWORD,
                            command.getEmail()
                    );

            if (alreadyExists) {
                throw new RegisterException("EMAIL_ALREADY_REGISTERED");
            }

        } else {
            // OAUTH (GOOGLE, GITHUB, etc.)
            if (command.getProviderUserId() == null || command.getProviderUserId().isBlank()) {
                throw new RegisterException("PROVIDER_USER_ID_REQUIRED");
            }

            boolean alreadyExists = userAuthProviderRepository
                    .existsByProviderAndProviderUserId(
                            command.getProvider(),
                            command.getProviderUserId()
                    );

            if (alreadyExists) {
                throw new RegisterException("USER_ALREADY_REGISTERED");
            }
        }

        /*
         * ===============================
         * CREATION UTILISATEUR
         * ===============================
         */
        User user = new User(
                command.getName(),
                command.getEmail(),
                UserType.HUMAN
        );

        userRepository.save(user);

        /*
         * ===============================
         * CREDENTIALS (PASSWORD UNIQUEMENT)
         * ===============================
         */
        if (command.isPasswordAuth()) {
            String passwordHash = passwordHasher.hash(command.getPassword());

            userCredentialsRepository.save(
                    user.getId(),
                    passwordHash
            );
        }

        /*
         * ===============================
         * AUTH PROVIDER
         * ===============================
         */
        String providerUserId = command.isPasswordAuth()
                ? command.getEmail()
                : command.getProviderUserId();

        userAuthProviderRepository.save(
                user.getId(),
                command.getProvider(),
                providerUserId,
                command.getEmail()
        );

        /*
         * ===============================
         * ROLE PAR DEFAUT
         * ===============================
         */
        Role role = roleRepository.findByCode("USER")
                .orElseThrow(() -> new IllegalStateException("DEFAULT_ROLE_USER_NOT_CONFIGURED"));

        UserRole userRole = new UserRole(user.getId(), role.getId());

        userRoleRepository.save(userRole);

        return user;
    }
}
