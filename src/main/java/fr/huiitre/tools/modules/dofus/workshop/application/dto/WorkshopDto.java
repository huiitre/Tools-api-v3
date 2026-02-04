package fr.huiitre.tools.modules.dofus.workshop.application.dto;

import java.util.List;

public class WorkshopDto {
    
    private final Long id;
    private final String name;
    private final boolean active;

    private final List<WorkshopTagDto> tags;

    public WorkshopDto(
        Long id,
        String name,
        boolean active,
        List<WorkshopTagDto> tags
    ) {
        this.id = id;
        this.name = name;
        this.active = active;

        this.tags = tags;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public List<WorkshopTagDto> getTags() {
        return tags;
    }
}
