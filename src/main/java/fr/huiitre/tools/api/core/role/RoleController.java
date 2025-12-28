package fr.huiitre.tools.api.core.role;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.api.common.RequiredRole;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.core.role.usecase.GetAllRolesUseCase;
import fr.huiitre.tools.application.core.role.view.RoleView;

import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Core - Role")
@RestController
@RequestMapping("/roles")
public class RoleController {
    
    private final static Logger logger = LoggerFactory.getLogger(RoleController.class);

    private final GetAllRolesUseCase getAllRolesUseCase;

    public RoleController(
        GetAllRolesUseCase getAllRolesUseCase
    ) {
        this.getAllRolesUseCase = getAllRolesUseCase;
    }

    @RequiredRole(RoleCode.TECH)
    @GetMapping
    public List<RoleView> getAllRoles() {
        return getAllRolesUseCase.execute();
    }
}
