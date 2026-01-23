package fr.huiitre.tools.modules.dofus.item.application.view;

import java.util.List;

import fr.huiitre.tools.modules.dofus.itemtype.application.view.ItemTypeDto;

public class ItemView {
    private final Long id;
    private final Long assetId;
    private final Long gameVersionId;

    private final String name;
    private final Long level;
    private final String description;

    private final ItemTypeDto itemType;
    private final List<ItemImageDto> images;

    private final boolean hasRecipe;

    public ItemView(
            Long id,
            Long assetId,
            Long gameVersionId,
            String name,
            Long level,
            String description,
            ItemTypeDto itemType,
            List<ItemImageDto> images,
            boolean hasRecipe) {
        this.id = id;
        this.assetId = assetId;
        this.gameVersionId = gameVersionId;
        this.name = name;
        this.level = level;
        this.description = description;
        this.itemType = itemType;
        this.images = images;
        this.hasRecipe = hasRecipe;
    }

    public Long getId() {
        return id;
    }

    public Long getAssetId() {
        return assetId;
    }

    public Long getGameVersionId() {
        return gameVersionId;
    }

    public String getName() {
        return name;
    }

    public Long getLevel() {
        return level;
    }

    public String getDescription() {
        return description;
    }

    public ItemTypeDto getItemType() {
        return itemType;
    }

    public List<ItemImageDto> getImages() {
        return images;
    }

    public boolean isHasRecipe() {
        return hasRecipe;
    }
}