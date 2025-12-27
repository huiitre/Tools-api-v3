package fr.huiitre.tools.application.core.role.ports;

import fr.huiitre.tools.domain.core.role.Role;
import fr.huiitre.tools.domain.core.role.UserRole;

public interface UserRoleRepository {

    void save(UserRole userRole);

    // void changeUserRole(UserRole userRole, Role newRoleCode);

}
