package fr.huiitre.tools.modules.dofus.pricing.application.view;

public class ItemPriceDto {

    private final Long itemId;

    /* prix directs */
    private final Long userPrice;
    private final Long communityAveragePrice;
    private final Long lastUpdatedPrice;

    /* prix de craft */
    private final Long craftUserPrice;
    private final Long craftCommunityPrice;
    private final Long craftLastPrice;
    private final Long craftCalculatedPrice;

    public ItemPriceDto(
        Long itemId,
        Long userPrice,
        Long communityAveragePrice,
        Long lastUpdatedPrice,
        Long craftUserPrice,
        Long craftCommunityPrice,
        Long craftLastPrice,
        Long craftCalculatedPrice
    ) {
        this.itemId = itemId;
        this.userPrice = userPrice;
        this.communityAveragePrice = communityAveragePrice;
        this.lastUpdatedPrice = lastUpdatedPrice;
        this.craftUserPrice = craftUserPrice;
        this.craftCommunityPrice = craftCommunityPrice;
        this.craftLastPrice = craftLastPrice;
        this.craftCalculatedPrice = craftCalculatedPrice;
    }

    public Long getItemId() {
        return itemId;
    }

    public Long getUserPrice() {
        return userPrice;
    }

    public Long getCommunityAveragePrice() {
        return communityAveragePrice;
    }

    public Long getLastUpdatedPrice() {
        return lastUpdatedPrice;
    }

    public Long getCraftUserPrice() {
        return craftUserPrice;
    }

    public Long getCraftCommunityPrice() {
        return craftCommunityPrice;
    }

    public Long getCraftLastPrice() {
        return craftLastPrice;
    }

    public Long getCraftCalculatedPrice() {
        return craftCalculatedPrice;
    }
}
