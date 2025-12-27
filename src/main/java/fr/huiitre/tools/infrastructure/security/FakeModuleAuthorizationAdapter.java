package fr.huiitre.tools.infrastructure.security;

import fr.huiitre.tools.application.common.security.ports.ModuleAuthorizationPort;
import fr.huiitre.tools.application.core.module.ModuleCode;

public class FakeModuleAuthorizationAdapter implements ModuleAuthorizationPort {

    @Override
    public boolean hasAccess(String userId, ModuleCode moduleCode) {
        // TEMPORAIRE : tout est autorisé
        return true;
    }
}
