package fr.huiitre.tools.modules.dofus.itemtype.application.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.security.application.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.dofus.itemtype.application.ports.ItemTypeRepository;

@Service
@Transactional
public class ListItemTypeUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final ItemTypeRepository itemTypeRepository;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.empty();
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.READ_ONLY;
    }

    public ListItemTypeUseCase(
            ItemTypeRepository itemTypeRepository,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.itemTypeRepository = itemTypeRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public void execute() {

    }
}