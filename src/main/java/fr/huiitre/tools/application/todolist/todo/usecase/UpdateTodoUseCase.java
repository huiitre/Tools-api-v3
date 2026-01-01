package fr.huiitre.tools.application.todolist.todo.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.todolist.todo.command.UpdateTodoCommand;
import fr.huiitre.tools.application.todolist.todo.ports.TodoRepository;
import fr.huiitre.tools.domain.todolist.todo.Todo;

@Service
@Transactional
public class UpdateTodoUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final TodoRepository todoRepository;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.TOOLS_TODOLIST);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    public UpdateTodoUseCase(
        TodoRepository todoRepository,
        AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.todoRepository = todoRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public void execute(Long todoId, UpdateTodoCommand command) {
        Long userId = authenticatedUserProvider.getUserId();

        Todo todo = todoRepository.findById(userId, todoId)
            .orElseThrow(() -> new IllegalArgumentException("Todo not found"));

        todo.update(
            command.getName(),
            command.getDescription(),
            command.isCompleted(),
            command.getDisplayOrder(),
            command.getPriority()
        );

        todoRepository.update(userId, todo);
    }
}