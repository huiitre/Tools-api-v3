package fr.huiitre.tools.application.core.user_module.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.core.user_module.command.RevokeUserModuleAccessCommand;
import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.domain.core.user_module.UserModuleRole;

@Service
@Transactional
public class RevokeUserModuleAccessUseCase implements SecuredUseCase {

    @Override
    public RoleCode requiredRole() {
        return RoleCode.ADMIN;
    }

    private final UserModuleRoleRepository userModuleRoleRepository;

    public RevokeUserModuleAccessUseCase(UserModuleRoleRepository userModuleRoleRepository) {
        this.userModuleRoleRepository = userModuleRoleRepository;
    }

    public void execute(RevokeUserModuleAccessCommand command) {
        UserModuleRole userModuleRole = userModuleRoleRepository
                .findByUserIdAndModuleId(command.getUserId(), command.getModuleId())
                .orElseThrow(() -> new IllegalArgumentException("USER_MODULE_ROLE_NOT_FOUND"));

        userModuleRoleRepository.deleteByUserIdAndModuleId(userModuleRole);
    }
}
