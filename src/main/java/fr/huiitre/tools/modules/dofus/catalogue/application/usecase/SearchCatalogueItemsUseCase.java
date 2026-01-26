package fr.huiitre.tools.modules.dofus.catalogue.application.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.core.security.application.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.dofus.assets.application.view.AssetImageUrlBuilder;
import fr.huiitre.tools.modules.dofus.assets.application.view.AssetResolution;
import fr.huiitre.tools.modules.dofus.catalogue.api.dto.CatalogueSearchQuery;
import fr.huiitre.tools.modules.dofus.catalogue.application.data.CatalogueColumnsDefinition;
import fr.huiitre.tools.modules.dofus.catalogue.application.dto.CatalogueItemDto;
import fr.huiitre.tools.modules.dofus.catalogue.application.dto.CatalogueSearchResponse;
import fr.huiitre.tools.modules.dofus.catalogue.application.ports.CatalogueItemRepository;
import fr.huiitre.tools.modules.dofus.item.application.ports.ItemRepository;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemImageDto;

@Service
@Transactional(readOnly = true)
public class SearchCatalogueItemsUseCase implements SecuredUseCase {

    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 20;

    private final AuthenticatedUserProvider authenticatedUserProvider;
    private final CatalogueItemRepository catalogueItemRepository;
    private final ItemRepository itemRepository;
    private final AssetImageUrlBuilder assetImageUrlBuilder;

    public SearchCatalogueItemsUseCase(
        AuthenticatedUserProvider authenticatedUserProvider,
        CatalogueItemRepository catalogueItemRepository,
        ItemRepository itemRepository,
        AssetImageUrlBuilder assetImageUrlBuilder
    ) {
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.catalogueItemRepository = catalogueItemRepository;
        this.itemRepository = itemRepository;
        this.assetImageUrlBuilder = assetImageUrlBuilder;
    }

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.READ_ONLY;
    }

    public CatalogueSearchResponse execute(
        CatalogueSearchQuery query,
        Long gameServerId
    ) {
        Long userId = authenticatedUserProvider.getUserId();

        int page = query.getPage() == null || query.getPage() < 1
            ? DEFAULT_PAGE
            : query.getPage();

        int pageSize = query.getPageSize() == null || query.getPageSize() < 1
            ? DEFAULT_PAGE_SIZE
            : query.getPageSize();

        query.setPage(page);
        query.setPageSize(pageSize);

        List<CatalogueItemDto> items = catalogueItemRepository.search(
            query,
            userId,
            gameServerId
        );

        for (CatalogueItemDto item : items) {
            List<ItemImageDto> itemImages = itemRepository.findImageByItemId(item.getId());

            for (ItemImageDto image : itemImages) {
                String url = assetImageUrlBuilder.build(
                    "item",
                    image.getIconId(),
                    AssetResolution.fromDb(image.getResolution()));
                image.setUrl(url);
            }

            if (!itemImages.isEmpty()) {
                item.setImages(itemImages);
            }
        }

        long total = catalogueItemRepository.count(
            query,
            userId,
            gameServerId
        );

        int computedLastPage = (int) Math.max(
            1,
            Math.ceil((double) total / pageSize)
        );

        Integer previousPage =
            page > 1 ? page - 1 : null;

        Integer nextPage =
            page < computedLastPage ? page + 1 : null;

        Integer lastPage =
            page < computedLastPage ? computedLastPage : null;

        return new CatalogueSearchResponse(
            CatalogueColumnsDefinition.all(),
            items,
            page,
            pageSize,
            total,
            previousPage,
            nextPage,
            lastPage
        );
    }
}
