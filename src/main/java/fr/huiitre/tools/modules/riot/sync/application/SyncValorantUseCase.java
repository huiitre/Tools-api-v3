package fr.huiitre.tools.modules.riot.sync.application;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;

@Service
@Transactional
public class SyncValorantUseCase implements SecuredUseCase {

    private final SyncValorantSkinsUseCase syncSkinsUseCase;
    private final SyncValorantBundlesUseCase syncBundlesUseCase;

    public SyncValorantUseCase(
            SyncValorantSkinsUseCase syncSkinsUseCase,
            SyncValorantBundlesUseCase syncBundlesUseCase) {
        this.syncSkinsUseCase = syncSkinsUseCase;
        this.syncBundlesUseCase = syncBundlesUseCase;
    }

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.RIOT);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    public ValorantGlobalSyncReport execute() {
        ValorantSyncReport skins = syncSkinsUseCase.execute();
        ValorantSyncReport bundles = syncBundlesUseCase.execute();
        return new ValorantGlobalSyncReport(skins, bundles);
    }
}
