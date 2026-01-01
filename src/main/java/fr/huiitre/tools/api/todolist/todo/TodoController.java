package fr.huiitre.tools.api.todolist.todo;

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
import fr.huiitre.tools.application.todolist.todo.command.CreateTodoCommand;
import fr.huiitre.tools.application.todolist.todo.command.UpdateTodoCommand;
import fr.huiitre.tools.application.todolist.todo.usecase.CreateTodoUseCase;
import fr.huiitre.tools.application.todolist.todo.usecase.DeleteTodoUseCase;
import fr.huiitre.tools.application.todolist.todo.usecase.ListUserTodoUseCase;
import fr.huiitre.tools.application.todolist.todo.usecase.UpdateTodoUseCase;
import fr.huiitre.tools.domain.todolist.todo.Todo;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Todolist - Todo")
@RestController
@RequestMapping("/todolists/{todolistId}/todos")
public class TodoController {
    
    private final Logger logger = LoggerFactory.getLogger(TodoController.class);

    private final CreateTodoUseCase createTodoUseCase;
    private final UpdateTodoUseCase updateTodoUseCase;
    private final ListUserTodoUseCase listUserTodoUseCase;
    private final DeleteTodoUseCase deleteTodoUseCase;

    public TodoController(
        CreateTodoUseCase createTodoUseCase,
        UpdateTodoUseCase updateTodoUseCase,
        ListUserTodoUseCase listUserTodoUseCase,
        DeleteTodoUseCase deleteTodoUseCase
    ) {
        this.createTodoUseCase = createTodoUseCase;
        this.updateTodoUseCase = updateTodoUseCase;
        this.listUserTodoUseCase = listUserTodoUseCase;
        this.deleteTodoUseCase = deleteTodoUseCase;
    }

    @RequiredRole(RoleCode.USER)
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void createTodo(
        @PathVariable Long todolistId,
        @RequestBody CreateTodoCommand command
    ) {
        createTodoUseCase.execute(todolistId, command);
    }

    @RequiredRole(RoleCode.USER)
    @GetMapping
    public List<Todo> listTodos(@PathVariable Long todolistId) {
        return listUserTodoUseCase.execute(todolistId);
    }

    @RequiredRole(RoleCode.USER)
    @PatchMapping("/{todoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateTodo(
        @PathVariable Long todolistId,
        @PathVariable Long todoId,
        @RequestBody UpdateTodoCommand command
    ) {
        updateTodoUseCase.execute(todolistId, todoId, command);
    }

    @RequiredRole(RoleCode.USER)
    @DeleteMapping("/{todoId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTodo(
        @PathVariable Long todolistId,
        @PathVariable Long todoId
    ) {
        deleteTodoUseCase.execute(todolistId, todoId);
    }
}