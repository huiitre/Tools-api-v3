package fr.huiitre.tools.application.test.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;

@Service
@Transactional
public class TestRightUseCase implements SecuredUseCase {

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.TEST);
        // return Optional.empty();
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.ADMIN;
    }

    public String execute() {

        return "ok";
    }
}
