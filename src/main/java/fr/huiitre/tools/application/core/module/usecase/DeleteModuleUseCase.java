package fr.huiitre.tools.application.core.module.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ports.ModuleRepository;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.domain.core.module.Module;

@Service
@Transactional
public class DeleteModuleUseCase implements SecuredUseCase {

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    private final ModuleRepository moduleRepository;
    private final UserModuleRoleRepository userModuleRoleRepository;

    public DeleteModuleUseCase(
            ModuleRepository moduleRepository,
            UserModuleRoleRepository userModuleRoleRepository) {
        this.moduleRepository = moduleRepository;
        this.userModuleRoleRepository = userModuleRoleRepository;
    }

    public void execute(Long moduleId) {

        // * vérification si le module existe */
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("MODULE_NOT_FOUND"));

        // * suppression des utilisateurs utilisant ce module */
        userModuleRoleRepository.deleteByModuleId(moduleId);

        // * suppression du module */
        moduleRepository.delete(module);
    }
}
