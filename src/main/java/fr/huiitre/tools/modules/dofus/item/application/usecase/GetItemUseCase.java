package fr.huiitre.tools.modules.dofus.item.application.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.modules.core.security.application.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.modules.core.security.application.usecase.SecuredUseCase;
import fr.huiitre.tools.modules.core.module.domain.ModuleCode;
import fr.huiitre.tools.modules.core.role.domain.RoleCode;
import fr.huiitre.tools.modules.dofus.assets.application.view.AssetImageUrlBuilder;
import fr.huiitre.tools.modules.dofus.assets.application.view.AssetResolution;
import fr.huiitre.tools.modules.dofus.item.application.ports.ItemRepository;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemImageDto;
import fr.huiitre.tools.modules.dofus.item.application.view.ItemView;
import fr.huiitre.tools.modules.dofus.item.domain.Item;
import fr.huiitre.tools.modules.dofus.itemtype.application.view.ItemTypeDto;

@Service
@Transactional
public class GetItemUseCase implements SecuredUseCase {

    private final ItemRepository itemRepository;
    private final AssetImageUrlBuilder assetImageUrlBuilder;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.DOFUS);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.READ_ONLY;
    }

    public GetItemUseCase(
            ItemRepository itemRepository,
            AuthenticatedUserProvider authenticatedUserProvider,
            AssetImageUrlBuilder assetImageUrlBuilder) {
        this.itemRepository = itemRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.assetImageUrlBuilder = assetImageUrlBuilder;
    }

    public Optional<ItemView> execute(Long gameVersionId, Long itemId) {

        Long userId = authenticatedUserProvider.getUserId();

        // * récupération de l'objet */
        ItemView itemView = itemRepository.findById(itemId, gameVersionId, userId);

        if (itemView == null) {
            return Optional.empty();
        }

        // * check métier */
        Item item = Item.rehydrate(
                itemView.getId(),
                itemView.getAssetId(),
                gameVersionId,
                itemView.getItemType().getId(),
                itemView.getName(),
                itemView.getLevel(),
                itemView.getDescription());

        ItemTypeDto itemType = new ItemTypeDto(
                itemView.getItemType().getId(),
                itemView.getItemType().getAssetId(),
                itemView.getItemType().getGameVersionId(),
                itemView.getItemType().getName());

        List<ItemImageDto> itemImages = itemRepository.findImageByItemId(item.getId());

        for (ItemImageDto image : itemImages) {
            String url = assetImageUrlBuilder.build(
                    "item",
                    image.getIconId(),
                    AssetResolution.fromDb(image.getResolution()));
            image.setUrl(url);
        }

        return Optional.of(
                new ItemView(
                        item.getId(),
                        item.getAssetId(),
                        item.getGameVersionId(),
                        item.getName(),
                        item.getLevel(),
                        item.getDescription(),
                        itemType,
                        itemImages,
                        itemView.isHasRecipe()));
    }
}