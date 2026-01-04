package fr.huiitre.tools.application.dofus.itemtype;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.dofus.ports.repositories.ItemTypeRepository;

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