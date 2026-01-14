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
         * VALIDATIONS
         * ===============================
         */
        if (command.getName() == null || command.getName().isBlank()) {
            throw new RegisterException("NAME_REQUIRED");
        }

        if (command.getEmail() == null || command.getEmail().isBlank()) {
            throw new RegisterException("EMAIL_REQUIRED");
        }

        if (command.getPassword() == null || command.getPassword().isBlank()) {
            throw new RegisterException("PASSWORD_REQUIRED");
        }

        /*
         * ===============================
         * REGLE FONDAMENTALE
         * ===============================
         * Si l'email existe déjà → REGISTER INTERDIT
         */
        if (userRepository.findByEmail(command.getEmail()).isPresent()) {
            throw new RegisterException("EMAIL_ALREADY_REGISTERED");
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
         * CREDENTIALS
         * ===============================
         */
        String passwordHash = passwordHasher.hash(command.getPassword());

        userCredentialsRepository.save(
                user.getId(),
                passwordHash
        );

        /*
         * ===============================
         * AUTH PROVIDER : PASSWORD
         * ===============================
         */
        userAuthProviderRepository.save(
                user.getId(),
                AuthProvider.PASSWORD,
                command.getEmail(),   // provider_user_id
                command.getEmail()    // provider_email (indicatif)
        );

        /*
         * ===============================
         * ROLE PAR DEFAUT
         * ===============================
         */
        Role role = roleRepository.findByCode("USER")
                .orElseThrow(() -> new IllegalStateException("DEFAULT_ROLE_USER_NOT_CONFIGURED"));

        userRoleRepository.save(new UserRole(user.getId(), role.getId()));

        return user;
    }
}
