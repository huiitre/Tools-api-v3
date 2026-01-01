package fr.huiitre.tools.application.todolist.todo.command;

import fr.huiitre.tools.domain.todolist.todo.TodoPriority;

public class CreateTodoCommand {
    
    public String name;
    public String description;
    public Boolean completed;
    public Long displayOrder;
    public TodoPriority priority;

    public CreateTodoCommand(
        String name,
        String description,
        Boolean completed,
        Long displayOrder,
        TodoPriority priority
    ) {
        this.name = name;
        this.description = description;
        this.completed = completed;
        this.displayOrder = displayOrder;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }
    public Boolean isCompleted() {
        return completed;
    }
    public Long getDisplayOrder() {
        return displayOrder;
    }
    public TodoPriority getPriority() {
        return priority;
    }
}
