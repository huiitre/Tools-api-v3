package fr.huiitre.tools.modules.dofus.workshop.application.dto;

import java.util.List;

public class WorkshopDetailResponse {
    
    List<WorkshopItemDetailDto> items;
    boolean isOwner;

    public WorkshopDetailResponse(List<WorkshopItemDetailDto> items, boolean isOwner) {
        this.items = items;
        this.isOwner = isOwner;
    }

    public List<WorkshopItemDetailDto> getItems() {
        return items;
    }

    public boolean isOwner() {
        return isOwner;
    }
}
