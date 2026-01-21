package fr.huiitre.tools.application.dofus.ports.repositories;

import java.util.List;
import java.util.Optional;

import fr.huiitre.tools.application.dofus.game.GameVersionData;

public interface GameVersionRepository {

    Optional<GameVersionData> findById(Long gameVersionId);

    GameVersionData findByCode(String code);

    List<GameVersionData> findAll();
}
