package fr.huiitre.tools.application.todolist.todolist.command;

public class UpdateTodolistCommand {
    private final String name;
    private final boolean active;
    private final boolean favorite;
    private final String colorHex;
    private final Long displayOrder;

    public UpdateTodolistCommand(
        String name,
        boolean active,
        boolean favorite,
        String colorHex,
        Long displayOrder
    ) {
        this.name = name;
        this.active = active;
        this.favorite = favorite;
        this.colorHex = colorHex;
        this.displayOrder = displayOrder;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public String getColorHex() {
        return colorHex;
    }

    public Long getDisplayOrder() {
        return displayOrder;
    }
}