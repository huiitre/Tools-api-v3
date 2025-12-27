package fr.huiitre.tools.application.common.security.usecase;

import java.util.Optional;

import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;

public interface SecuredUseCase {

    default Optional<ModuleCode> requiredModule() {
        return Optional.empty();
    }

    default RoleCode requiredRole() {
        return RoleCode.READ_ONLY;
    }
}
