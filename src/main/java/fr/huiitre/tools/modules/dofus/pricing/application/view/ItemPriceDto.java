package fr.huiitre.tools.modules.dofus.pricing.application.view;

import java.time.LocalDateTime;

public class ItemPriceDto {

    private final Long itemId;
    private final Long userPrice;
    private final Long communityAveragePrice;
    private final Long lastUpdatedPrice;

    public ItemPriceDto(
            Long itemId,
            Long userPrice,
            Long communityAveragePrice,
            Long lastUpdatedPrice) {
        this.itemId = itemId;
        this.userPrice = userPrice;
        this.communityAveragePrice = communityAveragePrice;
        this.lastUpdatedPrice = lastUpdatedPrice;
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
}
