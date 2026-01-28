package fr.huiitre.tools.modules.dofus.item.application.ports;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import fr.huiitre.tools.modules.dofus.item.application.view.ItemImageDto;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemView;
import fr.huiitre.tools.modules.dofus.item.domain.Item;

public interface ItemRepository {

    List<Item> findAllByGameVersionId(Long gameVersionId);

    Long save(Item item);

    void update(Item item);

    boolean refreshImages(Long itemId, Long iconId);

    Optional<Item> findByAssetId(Long assetId, long gameVersionId);

    ItemView findById(Long itemId, Long gameVersionId, Long userId);

    List<ItemImageDto> findImageByItemId(Long itemId);

    Map<Long, ItemView> findByGameVersionIdAndItemIds(Long gameVersionId, Set<Long> itemIds);
}
