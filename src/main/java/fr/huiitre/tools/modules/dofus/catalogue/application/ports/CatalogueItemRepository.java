package fr.huiitre.tools.modules.dofus.catalogue.application.ports;

import java.util.List;

import fr.huiitre.tools.modules.dofus.catalogue.api.dto.CatalogueSearchQuery;
import fr.huiitre.tools.modules.dofus.catalogue.application.dto.CatalogueItemDto;

public interface CatalogueItemRepository {
    
    public List<CatalogueItemDto> search(
        CatalogueSearchQuery query,
        Long userId,
        Long gameServerId
    );

    public Long count(
        CatalogueSearchQuery query,
        Long userId,
        Long gameServerId
    );
}
