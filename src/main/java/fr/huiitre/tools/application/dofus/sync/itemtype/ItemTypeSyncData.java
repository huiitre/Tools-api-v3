package fr.huiitre.tools.application.dofus.sync.itemtype;

public class ItemTypeSyncData {
    
    private final Long assetId;
    private final String name;
    private final Long categoryId;

    public ItemTypeSyncData(
        Long assetId,
        String name,
        Long categoryId
    ) {
        this.assetId = assetId;
        this.name = name;
        this.categoryId = categoryId;
    }

    public Long getAssetId() {
        return assetId;
    }

    public String getName() {
        return name;
    }

    public Long getCategoryId() {
        return categoryId;
    }
}
