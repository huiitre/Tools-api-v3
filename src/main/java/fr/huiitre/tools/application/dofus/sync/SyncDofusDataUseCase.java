package fr.huiitre.tools.application.dofus.sync;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.dofus.game.GameVersionData;
import fr.huiitre.tools.application.dofus.ports.repositories.GameVersionRepository;

@Service
@Transactional
public class SyncDofusDataUseCase implements SecuredUseCase {

    private final GameVersionRepository gameVersionRepository;

    private final SyncDofus3DataUseCase syncDofus3DataUseCase;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    public SyncDofusDataUseCase(
            SyncDofus3DataUseCase syncDofus3DataUseCase,
            GameVersionRepository gameVersionRepository) {
        this.syncDofus3DataUseCase = syncDofus3DataUseCase;
        this.gameVersionRepository = gameVersionRepository;
    }

    public void execute(
            Long gameVersionId) {
        // * récupération de la version */
        GameVersionData gameVersion = gameVersionRepository.findById(gameVersionId)
                .orElseThrow(() -> new IllegalArgumentException("Game version not found: " + gameVersionId));

        switch (gameVersion.getCode()) {
            case "dofus3" -> {
                syncDofus3DataUseCase.execute(gameVersion);
            }
            case "retro" -> {

            }
            default -> throw new IllegalArgumentException("Unsupported game version: " + gameVersion.getCode());
        }
    }
}