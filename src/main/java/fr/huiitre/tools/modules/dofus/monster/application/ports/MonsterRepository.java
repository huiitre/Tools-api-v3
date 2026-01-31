package fr.huiitre.tools.modules.dofus.monster.application.ports;

import java.util.List;
import java.util.Set;

import fr.huiitre.tools.modules.dofus.monster.domain.Monster;

public interface MonsterRepository {
    
    List<Monster> findAllByGameVersionId(Long gameVersionId);

    void update(Monster monster);

    Long insert(Monster monster);

    boolean refreshImages(Long monsterId, Long iconId);

    boolean refreshSubareas(Long monsterId, Set<Long> subareaIds);

    boolean refreshDrops(Long monsterId, Set<Long> itemIds);
}
