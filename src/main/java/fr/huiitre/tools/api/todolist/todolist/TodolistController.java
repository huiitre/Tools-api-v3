package fr.huiitre.tools.api.todolist.todolist;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.huiitre.tools.api.common.RequiredRole;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.todolist.todolist.command.CreateTodolistCommand;
import fr.huiitre.tools.application.todolist.todolist.command.UpdateTodolistCommand;
import fr.huiitre.tools.application.todolist.todolist.usecase.CreateTodolistUseCase;
import fr.huiitre.tools.application.todolist.todolist.usecase.DeleteTodolistUseCase;
import fr.huiitre.tools.application.todolist.todolist.usecase.ListUserTodolistsUseCase;
import fr.huiitre.tools.application.todolist.todolist.usecase.UpdateTodolistUseCase;
import fr.huiitre.tools.domain.todolist.todolist.Todolist;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Todolist - Todolists")
@RestController
@RequestMapping("/todolists")
public class TodolistController {
    
    private final Logger logger = LoggerFactory.getLogger(TodolistController.class);

    private final CreateTodolistUseCase createTodolistUseCase;
    private final UpdateTodolistUseCase updateTodolistUseCase;
    private final ListUserTodolistsUseCase listUserTodolistsUseCase;
    private final DeleteTodolistUseCase deleteTodolistUseCase;

    public TodolistController(
        CreateTodolistUseCase createTodolistUseCase,
        UpdateTodolistUseCase updateTodolistUseCase,
        ListUserTodolistsUseCase listUserTodolistsUseCase,
        DeleteTodolistUseCase deleteTodolistUseCase
    ) {
        this.createTodolistUseCase = createTodolistUseCase;
        this.updateTodolistUseCase = updateTodolistUseCase;
        this.listUserTodolistsUseCase = listUserTodolistsUseCase;
        this.deleteTodolistUseCase = deleteTodolistUseCase;
    }

    @RequiredRole(RoleCode.USER)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTodolist(
        @RequestBody CreateTodolistCommand command
    ) {
        createTodolistUseCase.execute(command);
    }

    @RequiredRole(RoleCode.USER)
    @GetMapping
    public List<Todolist> listTodolists() {
        return listUserTodolistsUseCase.execute();
    }

    @RequiredRole(RoleCode.USER)
    @PatchMapping("/{todolistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTodolist(
        @PathVariable Long todolistId,
        @RequestBody UpdateTodolistCommand command
    ) {
        updateTodolistUseCase.execute(todolistId, command);
    }

    @RequiredRole(RoleCode.USER)
    @DeleteMapping("/{todolistId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodolist(
        @PathVariable Long todolistId
    ) {
        deleteTodolistUseCase.execute(todolistId);
    }
}