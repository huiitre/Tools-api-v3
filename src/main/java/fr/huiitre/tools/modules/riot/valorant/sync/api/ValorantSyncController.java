package fr.huiitre.tools.modules.riot.valorant.sync.api;

import fr.huiitre.tools.modules.core.common.api.RequiredRole;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.riot.valorant.sync.application.SyncValorantSkinsUseCase;
import fr.huiitre.tools.modules.riot.valorant.sync.application.ValorantSyncReport;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/riot/valorant/sync")
public class ValorantSyncController {

    private final SyncValorantSkinsUseCase syncValorantSkinsUseCase;

    public ValorantSyncController(SyncValorantSkinsUseCase syncValorantSkinsUseCase) {
        this.syncValorantSkinsUseCase = syncValorantSkinsUseCase;
    }

    @RequiredRole(RoleCode.TECH)
    @PostMapping("/skins")
    @ResponseStatus(HttpStatus.OK)
    public ValorantSyncReport syncSkins() {
        return syncValorantSkinsUseCase.execute();
    }
}
