package fr.huiitre.tools.modules.dofus.catalogue.application.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.dofus.catalogue.application.ports.CatalogueItemRepository;
import fr.huiitre.tools.modules.dofus.item.application.ports.ItemRepository;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemImageDto;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemView;
import fr.huiitre.tools.modules.dofus.sync.application.views.AssetImageUrlBuilder;
import fr.huiitre.tools.modules.dofus.sync.application.views.AssetResolution;

@Service
@Transactional(readOnly = true)  // ✅ Corrigé
public class GetCatalogueRecipeItemUseCase implements SecuredUseCase {

    private final CatalogueItemRepository catalogueItemRepository;
    private final ItemRepository itemRepository;
    private final AssetImageUrlBuilder assetImageUrlBuilder;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.READ_ONLY;
    }

    public GetCatalogueRecipeItemUseCase(
            CatalogueItemRepository catalogueItemRepository,
            ItemRepository itemRepository,
            AssetImageUrlBuilder assetImageUrlBuilder) {
        this.catalogueItemRepository = catalogueItemRepository;
        this.itemRepository = itemRepository;
        this.assetImageUrlBuilder = assetImageUrlBuilder;
    }

    public List<ItemView> execute(Long itemId) {

        List<ItemView> ingredients = catalogueItemRepository.findRecipeByItemId(itemId);

        for (int i = 0; i < ingredients.size(); i++) {  // ✅ Corrigé
            ItemView ingredient = ingredients.get(i);

            List<ItemImageDto> images = itemRepository.findImageByItemId(ingredient.getId());

            for (ItemImageDto image : images) {
                String url = assetImageUrlBuilder.build(
                    "item",
                    image.getIconId(),
                    AssetResolution.fromDb(image.getResolution())
                );
                image.setUrl(url);
            }

            ItemView itemWithImages = new ItemView(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getDescription(),
                ingredient.isHasRecipe(),
                ingredient.getAssetId(),
                ingredient.getGameVersionId(),
                ingredient.getLevel(),
                ingredient.getType(),
                images,  // ✅
                ingredient.getParentItemId(),
                ingredient.getQuantity(),
                ingredient.getFarmZones()
            );

            ingredients.set(i, itemWithImages);  // ✅ Corrigé
        }

        return ingredients;
    }
}