package fr.huiitre.tools.application.common.security.ports;

import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;

public interface UserRoleProvider {

    /**
     * Retourne le rôle effectif de l'utilisateur.
     * - Si moduleCode == null : rôle global (user_role)
     * - Sinon : rôle dans le module (user_module_role)
     */
    RoleCode getUserRole(String userId, ModuleCode moduleCode);
}
