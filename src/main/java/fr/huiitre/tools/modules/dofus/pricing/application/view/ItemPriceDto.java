package fr.huiitre.tools.modules.dofus.pricing.application.view;

public class ItemPriceDto {

    private final Long userPrice;
    private final Long communityAveragePrice;
    private final Long lastUpdatedPrice;

    public ItemPriceDto(
            Long userPrice,
            Long communityAveragePrice,
            Long lastUpdatedPrice) {
        this.userPrice = userPrice;
        this.communityAveragePrice = communityAveragePrice;
        this.lastUpdatedPrice = lastUpdatedPrice;
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
}
