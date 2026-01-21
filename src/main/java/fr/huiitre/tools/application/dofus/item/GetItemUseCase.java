package fr.huiitre.tools.application.dofus.item;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.dofus.assets.dofus3.AssetImageUrlBuilder;
import fr.huiitre.tools.application.dofus.assets.dofus3.AssetResolution;
import fr.huiitre.tools.application.dofus.itemtype.ItemTypeDto;
import fr.huiitre.tools.application.dofus.ports.repositories.ItemRepository;
import fr.huiitre.tools.domain.dofus.Item;

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

        ItemPriceDto itemPrice = new ItemPriceDto(
                itemView.getPrices().getUserPrice(),
                itemView.getPrices().getCommunityAveragePrice(),
                itemView.getPrices().getLastUpdatedPrice());

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
                itemPrice,
                itemView.isHasRecipe()
            )
        );
    }
}