package fr.huiitre.tools.application.dofus.ports.repositories;

import java.util.List;

import fr.huiitre.tools.application.dofus.game.GameServerData;

public interface GameServerRepository {
    
    List<GameServerData> findAllByGameVersionId(Long gameVersionId);
}
