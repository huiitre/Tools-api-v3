package fr.huiitre.tools.api.core.auth.dto;

import java.util.List;
import java.util.Set;

import fr.huiitre.tools.application.core.role.view.RoleView;
import fr.huiitre.tools.application.core.user_module.view.UserModuleView;

public class UserProfileDto {

    private final Long id;
    private final String email;
    private final String name;
    private final String userType;
    private final boolean active;
    private final String avatarUrl;
    private final List<RoleView> roles;
    private final List<UserModuleView> modules;

    public UserProfileDto(
            Long id,
            String email,
            String name,
            String userType,
            boolean active,
            String avatarUrl,
            List<RoleView> roles,
            List<UserModuleView> modules
    ) {
        this.id = id;
        this.email = email;
        this.name = name;
        this.userType = userType;
        this.active = active;
        this.avatarUrl = avatarUrl;
        this.roles = roles;
        this.modules = modules;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getName() { return name; }
    public String getUserType() { return userType; }
    public boolean isActive() { return active; }
    public String getAvatarUrl() { return avatarUrl; }
    public List<RoleView> getRoles() { return roles; }
    public List<UserModuleView> getModules() { return modules; }
}
