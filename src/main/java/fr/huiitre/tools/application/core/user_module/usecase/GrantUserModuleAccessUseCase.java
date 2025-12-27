package fr.huiitre.tools.application.core.user_module.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.user_module.command.GrantUserModuleAccessCommand;
import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.domain.core.role.Role;
import fr.huiitre.tools.domain.core.user_module.UserModuleRole;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.application.core.module.ports.ModuleRepository;

@Service
@Transactional
public class GrantUserModuleAccessUseCase implements SecuredUseCase {

    @Override
    public RoleCode requiredRole() {
        return RoleCode.ADMIN;
    }

    private final UserModuleRoleRepository userModuleRoleRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;

    public GrantUserModuleAccessUseCase(
        UserModuleRoleRepository userModuleRoleRepository,
        RoleRepository roleRepository,
        UserRepository userRepository,
        ModuleRepository moduleRepository
    ) {
        this.userModuleRoleRepository = userModuleRoleRepository;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
    }

    public void execute(GrantUserModuleAccessCommand command) {
        // * récupération du rôle par défaut */
        Role defaultRole = roleRepository.findByCode("READ_ONLY")
                .orElseThrow(() -> new IllegalArgumentException("DEFAULT_ROLE_NOT_FOUND"));

        if (userModuleRoleRepository.findByUserIdAndModuleId(command.getUserId(), command.getModuleId()).isPresent())
            throw new IllegalArgumentException("USER_ALREADY_HAS_ACCESS_TO_MODULE");

        if (!userRepository.findById(command.getUserId()).isPresent()) {
            throw new IllegalArgumentException("USER_NOT_FOUND");
        }

        if (!moduleRepository.findById(command.getModuleId()).isPresent()) {
            throw new IllegalArgumentException("MODULE_NOT_FOUND");
        }

        UserModuleRole userModuleRole = new UserModuleRole(
            command.getUserId(),
            command.getModuleId(),
            defaultRole.getId()
        );

        // * création de l'accès utilisateur au module */
        userModuleRoleRepository.save(userModuleRole);
    }
}
