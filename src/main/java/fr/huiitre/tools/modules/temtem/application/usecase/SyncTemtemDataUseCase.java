package fr.huiitre.tools.modules.temtem.application.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;

@Service
@Transactional
public class SyncTemtemDataUseCase implements SecuredUseCase {
    
    private final SyncTypesUseCase syncTypesUseCase;
    private final SyncTemtemUseCase syncTemtemUseCase;
    private final SyncTechniquesUseCase syncTechniquesUseCase;
    private final SyncTemtemTechniquesUseCase syncTemtemTechniquesUseCase;

    public SyncTemtemDataUseCase(
        SyncTypesUseCase syncTypesUseCase,
        SyncTemtemUseCase syncTemtemUseCase,
        SyncTechniquesUseCase syncTechniquesUseCase,
        SyncTemtemTechniquesUseCase syncTemtemTechniquesUseCase
    ) {
        this.syncTypesUseCase = syncTypesUseCase;
        this.syncTemtemUseCase = syncTemtemUseCase;
        this.syncTechniquesUseCase = syncTechniquesUseCase;
        this.syncTemtemTechniquesUseCase = syncTemtemTechniquesUseCase;
    }

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.TEMTEM);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.TECH;
    }

    public void execute() {

        // =====================================================
        // TYPES
        // =====================================================
        syncTypesUseCase.execute();

        // =====================================================
        // TEMTEM
        // =====================================================
        syncTemtemUseCase.execute();

        // =====================================================
        // TECHNIQUES
        // =====================================================
        syncTechniquesUseCase.execute();

        // =====================================================
        // TEMTEM TECHNIQUES
        // =====================================================
        syncTemtemTechniquesUseCase.execute();
    }
}
