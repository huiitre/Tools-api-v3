package fr.huiitre.tools.application.core.user_module;

import java.util.List;

import fr.huiitre.tools.domain.core.user_module.UserModuleRole;

public interface UserModuleRoleRepository {
    
    void save(UserModuleRole userModuleRole);

    List<UserModuleRole> findByUserId(Long userId);

    void deleteByUserIdAndModuleId(Long userId, Long moduleId);

    void updateRole(UserModuleRole userModuleRole);
}
