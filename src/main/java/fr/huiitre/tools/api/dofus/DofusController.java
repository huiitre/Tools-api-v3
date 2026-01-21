package fr.huiitre.tools.api.dofus;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.application.dofus.gameversion.GameVersionData;
import fr.huiitre.tools.application.dofus.gameversion.GetGameVersionsUseCase;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Dofus")
@RestController
@RequestMapping("/dofus")
public class DofusController {
    
    private static final Logger logger = LoggerFactory.getLogger(DofusController.class);

    private final GetGameVersionsUseCase getGameVersionsUseCase;

    public DofusController(
        GetGameVersionsUseCase getGameVersionsUseCase
    ) {
        this.getGameVersionsUseCase = getGameVersionsUseCase;
    }

    @GetMapping("/game-versions")
    public ResponseEntity<List<GameVersionData>> getGameVersions() {
        return ResponseEntity.ok(getGameVersionsUseCase.execute());
    }
}
