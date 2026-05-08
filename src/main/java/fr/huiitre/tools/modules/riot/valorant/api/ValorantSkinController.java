package fr.huiitre.tools.modules.riot.valorant.api;

import fr.huiitre.tools.modules.core.common.api.RequiredRole;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.riot.valorant.application.usecase.ListValorantSkinsUseCase;
import fr.huiitre.tools.modules.riot.valorant.application.view.ValorantSkinView;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/riot/valorant")
public class ValorantSkinController {

    private final ListValorantSkinsUseCase listValorantSkinsUseCase;

    public ValorantSkinController(ListValorantSkinsUseCase listValorantSkinsUseCase) {
        this.listValorantSkinsUseCase = listValorantSkinsUseCase;
    }

    @RequiredRole(RoleCode.READ_ONLY)
    @GetMapping("/skins")
    public List<ValorantSkinView> listSkins() {
        return listValorantSkinsUseCase.execute();
    }
}
