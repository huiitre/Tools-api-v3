package fr.huiitre.tools.application.core.user_module.ports;

import java.util.List;
import java.util.Optional;

import fr.huiitre.tools.domain.core.user_module.UserModuleRole;

public interface UserModuleRoleRepository {
    
    void save(UserModuleRole userModuleRole);

    void deleteByUserIdAndModuleId(UserModuleRole userModuleRole);

    Optional<UserModuleRole> findByUserIdAndModuleId(Long userId, Long moduleId);

    void deleteByModuleId(Long moduleId);

    void updateRoleId(UserModuleRole userModuleRole);
}
