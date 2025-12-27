package fr.huiitre.tools.api.core.module;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.api.core.module.dto.ChangeUserModuleRoleRequest;
import fr.huiitre.tools.application.core.module.command.CreateModuleCommand;
import fr.huiitre.tools.application.core.module.command.UpdateModuleCommand;
import fr.huiitre.tools.application.core.module.usecase.CreateModuleUseCase;
import fr.huiitre.tools.application.core.module.usecase.DeleteModuleUseCase;
import fr.huiitre.tools.application.core.module.usecase.UpdateModuleUseCase;
import fr.huiitre.tools.application.core.user_module.command.ChangeUserModuleRoleCommand;
import fr.huiitre.tools.application.core.user_module.command.GrantUserModuleAccessCommand;
import fr.huiitre.tools.application.core.user_module.command.RevokeUserModuleAccessCommand;
import fr.huiitre.tools.application.core.user_module.usecase.ChangeUserModuleRoleUseCase;
import fr.huiitre.tools.application.core.user_module.usecase.GrantUserModuleAccessUseCase;
import fr.huiitre.tools.application.core.user_module.usecase.RevokeUserModuleAccessUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Core - Module")
@RestController
@RequestMapping("/module")
public class ModuleController {

    private final CreateModuleUseCase createModuleUseCase;
    private final DeleteModuleUseCase deleteModuleUseCase;
    private final UpdateModuleUseCase updateModuleUseCase;
    private final ChangeUserModuleRoleUseCase changeUserModuleRoleUseCase;
    private final GrantUserModuleAccessUseCase grantUserModuleAccessUseCase;
    private final RevokeUserModuleAccessUseCase revokeUserModuleAccessUseCase;

    public ModuleController(
            CreateModuleUseCase createModuleUseCase,
            DeleteModuleUseCase deleteModuleUseCase,
            UpdateModuleUseCase updateModuleUseCase,
            ChangeUserModuleRoleUseCase changeUserModuleRoleUseCase,
            GrantUserModuleAccessUseCase grantUserModuleAccessUseCase,
            RevokeUserModuleAccessUseCase revokeUserModuleAccessUseCase) {
        this.createModuleUseCase = createModuleUseCase;
        this.deleteModuleUseCase = deleteModuleUseCase;
        this.updateModuleUseCase = updateModuleUseCase;
        this.changeUserModuleRoleUseCase = changeUserModuleRoleUseCase;
        this.grantUserModuleAccessUseCase = grantUserModuleAccessUseCase;
        this.revokeUserModuleAccessUseCase = revokeUserModuleAccessUseCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createModule(@RequestBody CreateModuleCommand command) {
        createModuleUseCase.execute(command);
    }

    @PutMapping("/{moduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateModule(
            @PathVariable Long moduleId,
            @RequestBody UpdateModuleCommand command) {
        updateModuleUseCase.execute(moduleId, command);
    }

    @DeleteMapping("/{moduleId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteModule(@PathVariable Long moduleId) {
        deleteModuleUseCase.execute(moduleId);
    }

    @PostMapping("/{moduleId}/users/{userId}")
    @ResponseStatus(HttpStatus.CREATED)
    public void grantUserAccess(
            @PathVariable Long moduleId,
            @PathVariable Long userId) {
        grantUserModuleAccessUseCase.execute(new GrantUserModuleAccessCommand(userId, moduleId));
    }

    @PutMapping("/{moduleId}/users/{userId}/role")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void changeUserModuleRole(
            @PathVariable Long moduleId,
            @PathVariable Long userId,
            @RequestBody ChangeUserModuleRoleRequest request) {
        changeUserModuleRoleUseCase.execute(new ChangeUserModuleRoleCommand(userId, moduleId, request.getRoleId()));
    }

    @DeleteMapping("/{moduleId}/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void revokeUserAccess(
            @PathVariable Long moduleId,
            @PathVariable Long userId) {
        revokeUserModuleAccessUseCase.execute(new RevokeUserModuleAccessCommand(userId, moduleId));
    }

    // * Liste des modules - TECH */

    // * Liste des modules par utilisateur - READ_ONLY */
}
