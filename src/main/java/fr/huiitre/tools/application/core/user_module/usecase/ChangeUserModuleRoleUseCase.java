package fr.huiitre.tools.application.core.user_module.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.user_module.command.ChangeUserModuleRoleCommand;
import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.domain.core.role.Role;
import fr.huiitre.tools.domain.core.user_module.UserModuleRole;

@Service
@Transactional
public class ChangeUserModuleRoleUseCase implements SecuredUseCase {

    @Override
    public RoleCode requiredRole() {
        return RoleCode.ADMIN;
    }

    private final UserModuleRoleRepository userModuleRoleRepository;
    private final RoleRepository roleRepository;

    public ChangeUserModuleRoleUseCase(
        UserModuleRoleRepository userModuleRoleRepository,
        RoleRepository roleRepository
    ) {
        this.userModuleRoleRepository = userModuleRoleRepository;
        this.roleRepository = roleRepository;
    }

    public void execute(ChangeUserModuleRoleCommand command) {

        // * est ce que l'utilisateur pour le module existe */
        UserModuleRole userModuleRole = userModuleRoleRepository
            .findByUserIdAndModuleId(command.getUserId(), command.getModuleId())
            .orElseThrow(() -> new IllegalArgumentException("USER_MODULE_ROLE_NOT_FOUND"));

        // * est ce que le rôle demandé existe */
        Role role = roleRepository.findById(command.getRoleId())
            .orElseThrow(() -> new IllegalArgumentException("ROLE_NOT_FOUND"));

        userModuleRole.changeRole(role.getId());

        userModuleRoleRepository.updateRoleId(userModuleRole);
    }
}
