package fr.huiitre.tools.modules.riot.valorant.api;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.modules.core.common.api.RequiredRole;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.riot.valorant.application.usecase.GetValorantSkinByLevelUseCase;
import fr.huiitre.tools.modules.riot.valorant.application.usecase.ListValorantSkinsUseCase;
import fr.huiitre.tools.modules.riot.valorant.application.view.ValorantSkinView;

@RestController
@RequestMapping("/riot/valorant")
public class ValorantSkinController {

    private final ListValorantSkinsUseCase listValorantSkinsUseCase;
    private final GetValorantSkinByLevelUseCase getSkinByLevelUseCase;

    public ValorantSkinController(
            ListValorantSkinsUseCase listValorantSkinsUseCase,
            GetValorantSkinByLevelUseCase getSkinByLevelUseCase) {
        this.listValorantSkinsUseCase = listValorantSkinsUseCase;
        this.getSkinByLevelUseCase = getSkinByLevelUseCase;
    }

    @RequiredRole(RoleCode.READ_ONLY)
    @GetMapping("/skins")
    public List<ValorantSkinView> listSkins() {
        return listValorantSkinsUseCase.execute();
    }

    @RequiredRole(RoleCode.READ_ONLY)
    @GetMapping("/skins/by-level/{levelAssetId}")
    public ValorantSkinView getSkinByLevel(@PathVariable UUID levelAssetId) {
        return getSkinByLevelUseCase.execute(levelAssetId);
    }
}
