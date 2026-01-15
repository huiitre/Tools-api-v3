package fr.huiitre.tools.application.core.user_module.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.application.core.user_module.view.UserModuleView;

@Service
@Transactional
public class GetUserModulesUseCase implements SecuredUseCase {

    @Override
    public RoleCode requiredRole() {
        return RoleCode.ADMIN;
    }

    private final UserModuleRoleRepository userModuleRoleRepository;

    public GetUserModulesUseCase(
        UserModuleRoleRepository userModuleRoleRepository
    ) {
        this.userModuleRoleRepository = userModuleRoleRepository;
    }

    public List<UserModuleView> execute(Long userId) {
        return userModuleRoleRepository.findAllByUserId(userId);
    }
}