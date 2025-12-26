package fr.huiitre.tools.domain.core.module;

public class Module {
    private Long id;
    private final String code;
    private final String name;
    private final String description;
    private boolean active;
    private final java.time.LocalDateTime createdAt;
    private java.time.LocalDateTime updatedAt;

    public Module(
        Long id,
        String code,
        String name,
        String description,
        boolean active,
        java.time.LocalDateTime createdAt,
        java.time.LocalDateTime updatedAt
    ) {
        this.id             = id;
        this.code           = code;
        this.name           = name;
        this.description    = description;
        this.active         = active;
        this.createdAt      = createdAt;
        this.updatedAt      = updatedAt;
    }

    public Long getId() {
      return this.id;
    }
    void setId(Long value) {
      this.id = value;
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

    public boolean getActive() {
      return this.active;
    }
    public void setActive(boolean value) {
      this.active = value;
    }

    public java.time.LocalDateTime getCreatedAt() {
      return this.createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt() {
      return this.updatedAt;
    }
    public void setUpdatedAt(java.time.LocalDateTime value) {
      this.updatedAt = value;
    }
}
