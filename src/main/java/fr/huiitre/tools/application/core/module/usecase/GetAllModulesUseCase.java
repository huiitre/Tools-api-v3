package fr.huiitre.tools.application.core.module.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ports.ModuleRepository;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.domain.core.module.Module;

@Service
@Transactional
public class GetAllModulesUseCase implements SecuredUseCase {

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    private final ModuleRepository moduleRepository;

    public GetAllModulesUseCase(
            ModuleRepository moduleRepository) {
        this.moduleRepository = moduleRepository;
    }

    public List<Module> execute() {
        return moduleRepository.findAll();
    }
}