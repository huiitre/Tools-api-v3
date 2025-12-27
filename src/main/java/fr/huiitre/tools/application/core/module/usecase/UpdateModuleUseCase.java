package fr.huiitre.tools.application.core.module.usecase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.command.UpdateModuleCommand;
import fr.huiitre.tools.application.core.module.ports.ModuleRepository;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.domain.core.module.Module;

@Service
@Transactional
public class UpdateModuleUseCase implements SecuredUseCase {

    private final static Logger logger = LoggerFactory.getLogger(UpdateModuleUseCase.class);

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    private final ModuleRepository moduleRepository;

    public UpdateModuleUseCase(
            ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public void execute(Long moduleId, UpdateModuleCommand command) {

        // * vérification si le module existe */
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("MODULE_NOT_FOUND"));

        // * mise à jour du module */
        module.update(
            command.getCode(),
            command.getName(),
            command.getDescription(),
            command.getActive()
        );

        moduleRepository.update(module);
    }
}
