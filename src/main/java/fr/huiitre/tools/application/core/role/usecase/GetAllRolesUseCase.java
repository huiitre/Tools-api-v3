package fr.huiitre.tools.application.core.role.usecase;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.role.view.RoleView;

@Service
@Transactional
public class GetAllRolesUseCase implements SecuredUseCase {
    
    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    private final RoleRepository roleRepository;

    public GetAllRolesUseCase(
            RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public List<RoleView> execute() {
        return roleRepository.findAll();
    }
}
