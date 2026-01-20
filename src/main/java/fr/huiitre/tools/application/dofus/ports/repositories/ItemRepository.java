package fr.huiitre.tools.application.dofus.ports.repositories;

import java.util.List;
import java.util.Optional;

import fr.huiitre.tools.domain.dofus.Item;

public interface ItemRepository {

    List<Item> findAllByGameVersionId(Long gameVersionId);

    Long save(Item item);

    void update(Item item);

    boolean refreshImages(Long itemId, Long iconId);

    Optional<Item> findByAssetId(Long assetId, long gameVersionId);
}
