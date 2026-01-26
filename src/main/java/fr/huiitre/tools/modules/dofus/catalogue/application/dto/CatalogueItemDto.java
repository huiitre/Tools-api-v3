package fr.huiitre.tools.modules.dofus.catalogue.application.dto;

import java.util.List;

import fr.huiitre.tools.modules.dofus.item.application.view.ItemImageDto;

public class CatalogueItemDto {

    private final long id;
    private final long assetId;
    private final String type;
    private final String name;
    private final String description;
    private final Long level;

    private List<ItemImageDto> images;

    private final boolean hasRecipe;

    public CatalogueItemDto(
        long id,
        long assetId,
        String type,
        String name,
        String description,
        Long level,
        boolean hasRecipe
    ) {
        this.id = id;
        this.assetId = assetId;
        this.type = type;
        this.name = name;
        this.description = description;
        this.level = level;
        this.hasRecipe = hasRecipe;
    }

    public long getId() {
        return id;
    }

    public long getAssetId() {
        return assetId;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Long getLevel() {
        return level;
    }

    public List<ItemImageDto> getImages() {
        return images;
    }

    public void setImages(List<ItemImageDto> images) {
        this.images = images;
    }

    public boolean isHasRecipe() {
        return hasRecipe;
    }
}
