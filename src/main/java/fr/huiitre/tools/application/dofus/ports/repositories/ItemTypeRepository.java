package fr.huiitre.tools.application.dofus.ports.repositories;

import java.util.List;

import fr.huiitre.tools.domain.dofus.itemtype.ItemType;

public interface ItemTypeRepository {

    List<ItemType> findAllByGameVersionId(Long gameVersionId);

    void save(ItemType itemType);

    void update(ItemType itemType);

}
