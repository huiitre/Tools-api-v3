package fr.huiitre.tools.application.core.role.ports;

import java.util.List;

import fr.huiitre.tools.application.core.role.view.RoleView;
import fr.huiitre.tools.domain.core.role.UserRole;

public interface UserRoleRepository {

    void save(UserRole userRole);

    List<RoleView> findAllByUserId(Long userId);
}
