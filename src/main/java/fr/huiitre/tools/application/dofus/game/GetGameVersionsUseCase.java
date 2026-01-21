package fr.huiitre.tools.application.dofus.game;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.dofus.ports.repositories.GameVersionRepository;

@Service
@Transactional
public class GetGameVersionsUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final GameVersionRepository gameVersionRepository;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.READ_ONLY;
    }

    public GetGameVersionsUseCase(
            GameVersionRepository gameVersionRepository,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.gameVersionRepository = gameVersionRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public List<GameVersionData> execute() {

        return gameVersionRepository.findAll();
    }
}