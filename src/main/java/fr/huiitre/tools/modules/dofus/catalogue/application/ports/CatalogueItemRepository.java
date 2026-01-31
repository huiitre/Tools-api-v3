package fr.huiitre.tools.modules.dofus.catalogue.application.ports;

import java.util.List;

import fr.huiitre.tools.modules.dofus.catalogue.api.dto.CatalogueSearchQuery;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemView;

public interface CatalogueItemRepository {
    
    public List<ItemView> search(
        CatalogueSearchQuery query,
        Long userId,
        Long gameServerId
    );

    public Long count(
        CatalogueSearchQuery query,
        Long userId,
        Long gameServerId
    );

    public List<ItemView> findRecipeByItemId(Long itemId);
}
