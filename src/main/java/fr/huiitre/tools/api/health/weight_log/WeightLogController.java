package fr.huiitre.tools.api.health.weight_log;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.api.common.RequiredRole;
import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.health.weight_log.command.CreateWeightLogCommand;
import fr.huiitre.tools.application.health.weight_log.command.UpdateWeightLogCommand;
import fr.huiitre.tools.application.health.weight_log.usecase.CreateWeightLogUseCase;
import fr.huiitre.tools.application.health.weight_log.usecase.DeleteWeightLogUseCase;
import fr.huiitre.tools.application.health.weight_log.usecase.GetAllMyWeightLogUseCase;
import fr.huiitre.tools.application.health.weight_log.usecase.UpdateWeightLogUseCase;
import fr.huiitre.tools.application.health.weight_log.view.WeightLogView;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Core - Weight Log")
@RestController
@RequestMapping("/weight-logs")
public class WeightLogController {
    
    private final CreateWeightLogUseCase createWeightLogUseCase;
    private final DeleteWeightLogUseCase deleteWeightLogUseCase;
    private final UpdateWeightLogUseCase updateWeightLogUseCase;
    private final GetAllMyWeightLogUseCase getAllMyWeightLogUseCase;

    public WeightLogController(
        CreateWeightLogUseCase createWeightLogUseCase,
        UpdateWeightLogUseCase updateWeightLogUseCase,
        DeleteWeightLogUseCase deleteWeightLogUseCase,
        GetAllMyWeightLogUseCase getAllMyWeightLogUseCase
    ) {
        this.createWeightLogUseCase = createWeightLogUseCase;
        this.updateWeightLogUseCase = updateWeightLogUseCase;
        this.deleteWeightLogUseCase = deleteWeightLogUseCase;
        this.getAllMyWeightLogUseCase = getAllMyWeightLogUseCase;
    }

    @RequiredRole(RoleCode.USER)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createWeightLog(@RequestBody CreateWeightLogCommand command) {
        createWeightLogUseCase.execute(command);
    }

    @RequiredRole(RoleCode.USER)
    @PatchMapping("/{weightLogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateWeightLog(
        @PathVariable Long weightLogId,
        @RequestBody UpdateWeightLogCommand command
    ) {
        updateWeightLogUseCase.execute(weightLogId, command);
    }

    @RequiredRole(RoleCode.USER)
    @DeleteMapping("/{weightLogId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteWeightLog(
        @PathVariable Long weightLogId
    ) {
        deleteWeightLogUseCase.execute(
            weightLogId
        );
    }

    @RequiredRole(RoleCode.USER)
    @GetMapping
    public List<WeightLogView> getWeightLogs() {
        return getAllMyWeightLogUseCase.execute();
    }
}
