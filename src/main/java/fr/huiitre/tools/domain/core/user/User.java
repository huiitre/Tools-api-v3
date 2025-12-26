package fr.huiitre.tools.domain.core.user;

import java.time.LocalDateTime;

public class User {

    private Long id;
    private final String name;
    private final String email;
    private final UserType userType;
    private boolean active;
    private final LocalDateTime createdAt;

    public User(String name, String email, UserType userType) {
        this.name = name;
        this.email = email;
        this.userType = userType;
        this.active = false;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public UserType getUserType() {
        return userType;
    }

    public boolean isActive() {
        return active;
    }

    public void setIsActive(boolean bool) {
        this.active = bool;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
