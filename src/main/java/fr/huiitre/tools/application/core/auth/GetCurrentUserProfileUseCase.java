package fr.huiitre.tools.application.core.auth;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.api.core.auth.dto.UserModuleDto;
import fr.huiitre.tools.api.core.auth.dto.UserProfileDto;
import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.auth.exception.UserNotFoundException;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;
import fr.huiitre.tools.application.core.role.view.RoleView;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.application.core.user_module.view.UserModuleView;
import fr.huiitre.tools.domain.core.role.Role;
import fr.huiitre.tools.domain.core.user.User;

@Service
@Transactional
public class GetCurrentUserProfileUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final UserModuleRoleRepository userModuleRoleRepository;

    public GetCurrentUserProfileUseCase(
        AuthenticatedUserProvider authenticatedUserProvider,
        UserRepository userRepository,
        UserRoleRepository userRoleRepository,
        UserModuleRoleRepository userModuleRoleRepository
    ) {
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.userModuleRoleRepository = userModuleRoleRepository;
    }

    public UserProfileDto execute() {
        Long userId = authenticatedUserProvider.getUserId();

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new UserNotFoundException("USER_NOT_FOUND"));

        List<RoleView> roles =
            userRoleRepository.findAllByUserId(userId);

        List<UserModuleView> modules =
            userModuleRoleRepository.findAllByUserId(userId);

        return new UserProfileDto(
            user.getId(),
            user.getEmail(),
            user.getName(),
            user.getUserType().name(),
            user.isActive(),
            roles,
            modules
        );
    }
}