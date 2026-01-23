package fr.huiitre.tools.modules.dofus.item.application.view;

public class ItemImageDto {

    private final Long id;
    private final Long itemId;
    private final String resolution; // X1 / X2
    private final Long iconId;
    private String url;

    public ItemImageDto(
            Long id,
            Long itemId,
            String resolution,
            Long iconId) {
        this.id = id;
        this.itemId = itemId;
        this.resolution = resolution;
        this.iconId = iconId;
    }

    public Long getId() {
        return id;
    }

    public Long getItemId() {
        return itemId;
    }

    public String getResolution() {
        return resolution;
    }

    public Long getIconId() {
        return iconId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
