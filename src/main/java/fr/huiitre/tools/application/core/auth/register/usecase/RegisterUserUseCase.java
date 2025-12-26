package fr.huiitre.tools.application.core.auth.register.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.core.auth.AuthProvider;
import fr.huiitre.tools.application.core.auth.PasswordHasher;
import fr.huiitre.tools.application.core.auth.exception.RegisterException;
import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;
import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.application.core.user.ports.UserCredentialsRepository;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.domain.core.role.Role;
import fr.huiitre.tools.domain.core.role.UserRole;
import fr.huiitre.tools.domain.core.user.User;
import fr.huiitre.tools.domain.core.user.UserType;

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
        PasswordHasher passwordHasher
    ) {
        this.userRepository = userRepository;
        this.userCredentialsRepository = userCredentialsRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
        this.passwordHasher = passwordHasher;
    }

    public User execute(RegisterUserCommand command) {

        if (command.getProvider() != AuthProvider.PASSWORD) {
            throw new RegisterException("UNSUPPORTED_AUTH_PROVIDER");
        }

        if (command.getName() == null || command.getName().isBlank()) {
            throw new RegisterException("NAME_REQUIRED");
        }

        boolean alreadyExists =
            userAuthProviderRepository.existsByProviderAndProviderUserId(
                AuthProvider.PASSWORD,
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
            AuthProvider.PASSWORD,
            command.getEmail(), // provider_user_id (PASSWORD = email)
            command.getEmail()  // provider_email
        );

        //* ajout du rôle pour le nouvel utilisateur */
        Role role = roleRepository.findByCode("USER")
            .orElseThrow(() -> new IllegalStateException("DEFAULT_ROLE_USER_NOT_CONFIGURED"));

        UserRole userRole = new UserRole(user.getId(), role.getId());
        
        userRoleRepository.save(userRole);

        return user;
    }
}
