package fr.huiitre.tools.application.core.module.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.command.CreateModuleCommand;
import fr.huiitre.tools.application.core.module.ports.ModuleRepository;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.domain.core.module.Module;

@Service
@Transactional
public class CreateModuleUseCase implements SecuredUseCase {

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    private final ModuleRepository moduleRepository;

    public CreateModuleUseCase(
            ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public Module execute(CreateModuleCommand command) {

        System.out.println("CreateModuleUseCase.execute : " + command.getCode());

        // * vérification si le module existe déjà (code) */
        if (moduleRepository.existsByCode(command.getCode()))
            throw new IllegalArgumentException("MODULE_ALREADY_EXISTS");

        // * le module n'existe pas, on peut le créer */
        Module module = Module.create(
                command.getCode(),
                command.getName(),
                command.getDescription());

        moduleRepository.save(module);

        return module;
    }
}
