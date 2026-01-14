package fr.huiitre.tools.application.core.auth;

import org.springframework.stereotype.Service;

import fr.huiitre.tools.application.core.auth.exception.UserDisabledException;
import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;
import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.domain.core.role.Role;
import fr.huiitre.tools.domain.core.role.UserRole;
import fr.huiitre.tools.domain.core.user.User;
import fr.huiitre.tools.domain.core.user.UserType;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class AuthenticateUserWithProviderUseCase {

    private final UserRepository userRepository;
    private final UserAuthProviderRepository userAuthProviderRepository;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;

    public AuthenticateUserWithProviderUseCase(
            UserRepository userRepository,
            UserAuthProviderRepository userAuthProviderRepository,
            UserRoleRepository userRoleRepository,
            RoleRepository roleRepository
    ) {
        this.userRepository = userRepository;
        this.userAuthProviderRepository = userAuthProviderRepository;
        this.userRoleRepository = userRoleRepository;
        this.roleRepository = roleRepository;
    }

    public User execute(AuthenticateWithProviderCommand command) {

        // 1. Vérifier si un lien provider existe déjà
        return userAuthProviderRepository
            .findUserIdByProviderAndProviderUserId(
                command.getProvider(),
                command.getProviderUserId()
            )
            .flatMap(userRepository::findById)
            .map(this::ensureActive)
            .orElseGet(() -> authenticateOrRegisterByEmail(command));
    }

    private User authenticateOrRegisterByEmail(AuthenticateWithProviderCommand command) {

        // 2. User existant par email → lier le provider
        return userRepository
            .findByEmail(command.getEmail())
            .map(this::ensureActive)
            .map(user -> linkProviderToExistingUser(user, command))
            // 3. Aucun user → création
            .orElseGet(() -> registerNewUser(command));
    }

    private User linkProviderToExistingUser(User user, AuthenticateWithProviderCommand command) {

        userAuthProviderRepository.save(
            user.getId(),
            command.getProvider(),
            command.getProviderUserId(),
            command.getEmail()
        );

        return user;
    }

    private User registerNewUser(AuthenticateWithProviderCommand command) {

        // 2. Création user
        User user = new User(
            command.getName(),
            command.getEmail(),
            UserType.HUMAN
        );

        user.setIsActive(true);

        userRepository.save(user);

        // 3. Lien provider
        userAuthProviderRepository.save(
            user.getId(),
            command.getProvider(),
            command.getProviderUserId(),
            command.getEmail()
        );

        // 4. Rôle USER
        Role role = roleRepository.findByCode("USER")
            .orElseThrow(() -> new IllegalStateException("DEFAULT_ROLE_USER_NOT_CONFIGURED"));

        userRoleRepository.save(new UserRole(user.getId(), role.getId()));

        return user;
    }

    private User ensureActive(User user) {
        if (!user.isActive()) {
            throw new UserDisabledException("Utilisateur désactivé");
        }
        return user;
    }
}
