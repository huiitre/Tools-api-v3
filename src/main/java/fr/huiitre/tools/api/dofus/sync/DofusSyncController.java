package fr.huiitre.tools.api.dofus.sync;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.api.common.RequiredRole;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.dofus.sync.SyncDofusDataUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Dofus - Sync")
@RestController
@RequestMapping("/dofus/{gameVersionId}/sync")
public class DofusSyncController {

    private final SyncDofusDataUseCase syncDofusDataUseCase;
    
    private final Logger logger = LoggerFactory.getLogger(DofusSyncController.class);

    public DofusSyncController(SyncDofusDataUseCase syncDofusDataUseCase) {
        this.syncDofusDataUseCase = syncDofusDataUseCase;

    }

    @RequiredRole(RoleCode.TECH)
    @PostMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void syncDofusData(
        @PathVariable Long gameVersionId
    ) {
        syncDofusDataUseCase.execute(gameVersionId);
    }
}
