package fr.huiitre.tools.modules.dofus.workshop.domain;

public class Workshop {
    
    private final Long id;
    private String name;
    private boolean active;
    private boolean pinned;

    private Workshop(Long id, String name, boolean active, boolean pinned) {
        validateName(name);
        this.id = id;
        this.name = name;
        this.active = active;
        this.pinned = pinned;
    }

    public static Workshop rehydrate(Long id, String name, boolean active, boolean pinned) {
        return new Workshop(id, name, active, pinned);
    }

    public static Workshop create(String name) {
        return new Workshop(null, name, true, false);
    }

    public void update(String name, boolean active, boolean pinned) {
        validateName(name);
        this.name = name;
        this.active = active;
        this.pinned = pinned;
    }

    private void validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Le nom de l'atelier ne peut pas être vide.");
        }

        if (name.length() > 30) {
            throw new IllegalArgumentException("Le nom de l'atelier ne peut pas dépasser 30 caractères.");
        }
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

    public boolean isPinned() {
        return pinned;
    }
}