package fr.huiitre.tools.application.common.security.ports;

import fr.huiitre.tools.application.core.module.ModuleCode;

public interface ModuleAuthorizationPort {

    boolean hasAccess(String userId, ModuleCode moduleCode);
}