package fr.huiitre.tools.application.core.user_module.view;

import java.time.LocalDateTime;

public class UserModuleView {
    private final Long moduleId;
    private final String code;
    private final String name;
    private final String description;
    private final boolean active;
    private final LocalDateTime createdAt;
    private final Long roleId;
    private final String roleCode;
    private final String roleName;
    private final String roleDescription;
    private final boolean roleActive;
    private final LocalDateTime roleCreatedAt;
    private final LocalDateTime roleUpdatedAt;

    public UserModuleView(
        Long moduleId,
        String code,
        String name,
        String description,
        boolean active,
        LocalDateTime createdAt,
        Long roleId,
        String roleCode,
        String roleName,
        String roleDescription,
        boolean roleActive,
        LocalDateTime roleCreatedAt,
        LocalDateTime roleUpdatedAt
    ) {
        this.moduleId = moduleId;
        this.code = code;
        this.name = name;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
        this.roleId = roleId;
        this.roleCode = roleCode;
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.roleActive = roleActive;
        this.roleCreatedAt = roleCreatedAt;
        this.roleUpdatedAt = roleUpdatedAt;
    }

    public Long getModuleId() {
        return this.moduleId;
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


    public LocalDateTime getCreatedAt() {
        return this.createdAt;
    }


    public Long getRoleId() {
        return this.roleId;
    }


    public String getRoleCode() {
        return this.roleCode;
    }


    public String getRoleName() {
        return this.roleName;
    }


    public String getRoleDescription() {
        return this.roleDescription;
    }


    public boolean isRoleActive() {
        return this.roleActive;
    }


    public LocalDateTime getRoleCreatedAt() {
        return this.roleCreatedAt;
    }


    public LocalDateTime getRoleUpdatedAt() {
        return this.roleUpdatedAt;
    }
}
