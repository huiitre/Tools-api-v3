package fr.huiitre.tools.modules.temtem.api;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.modules.core.common.api.RequiredRole;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.temtem.application.usecase.SyncTemtemDataUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Temtem")
@RestController
@RequestMapping("/temtem")
public class TemtemSyncController {

    private final SyncTemtemDataUseCase syncTemtemDataUseCase;

    public TemtemSyncController(
        SyncTemtemDataUseCase syncTemtemDataUseCase
    ) {
        this.syncTemtemDataUseCase = syncTemtemDataUseCase;
    }
    
    @RequiredRole(RoleCode.TECH)
    @PostMapping("/sync")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void syncTemtemData() {
        syncTemtemDataUseCase.execute();
    }
}
