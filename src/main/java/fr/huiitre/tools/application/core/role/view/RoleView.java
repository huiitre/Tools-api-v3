package fr.huiitre.tools.application.core.role.view;

import java.time.LocalDateTime;

public class RoleView {
    
    private final Long id;
    private final String code;
    private final String name;
    private final String description;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public RoleView(
        Long id,
        String code,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
    ) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return this.id;
    }


    public String getCode() {
        return this.code;
    }


    public String getName() {
        return this.name;
    }


    public String getDescription() {
        return this.description;
    }


    public boolean isActive() {
        return this.active;
    }

    public boolean getActive() {
        return this.active;
    }


    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return this.updatedAt;
    }
}
