package fr.huiitre.tools.modules.dofus.workshop.domain;

public class Workshop {
    
    private final Long id;
    private String name;
    private boolean active;

    private Workshop(Long id, String name, boolean active) {
        validateName(name);
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public static Workshop rehydrate(Long id, String name, boolean active) {
        return new Workshop(id, name, active);
    }

    public static Workshop create(String name) {
        return new Workshop(null, name, true);
    }

    public void update(String name, boolean active) {
        validateName(name);
        this.name = name;
        this.active = active;
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
}