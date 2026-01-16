package fr.huiitre.tools.application.core.auth;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.api.core.auth.dto.UserProfileDto;
import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.auth.exception.UserNotFoundException;
import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;
import fr.huiitre.tools.application.core.role.view.RoleView;
import fr.huiitre.tools.application.core.user.ports.AvatarResolver;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.application.core.user_module.view.UserModuleView;
import fr.huiitre.tools.domain.core.user.User;

@Service
@Transactional
public class GetCurrentUserProfileUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserModuleRoleRepository userModuleRoleRepository;
    private final AvatarResolver avatarResolver;

    public GetCurrentUserProfileUseCase(
        AuthenticatedUserProvider authenticatedUserProvider,
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        UserModuleRoleRepository userModuleRoleRepository,
        AvatarResolver avatarResolver
    ) {
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.userModuleRoleRepository = userModuleRoleRepository;
        this.avatarResolver = avatarResolver;
    }

    public UserProfileDto execute() {
        Long userId = authenticatedUserProvider.getUserId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        List<RoleView> roles =
            userRoleRepository.findAllByUserId(userId);

        List<UserModuleView> modules =
            userModuleRoleRepository.findAllByUserId(userId);

        //* récupération de l'avatarUrl */
        String avatarUrl = avatarResolver.resolve(user);

        return new UserProfileDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getUserType().name(),
            user.isActive(),
            avatarUrl,
            roles,
            modules
        );
    }
}