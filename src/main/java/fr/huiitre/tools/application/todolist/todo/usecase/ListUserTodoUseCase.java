package fr.huiitre.tools.application.todolist.todo.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.todolist.todo.ports.TodoRepository;
import fr.huiitre.tools.domain.todolist.todo.Todo;

@Service
@Transactional
public class ListUserTodoUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    private final TodoRepository todoRepository;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.TODOLIST);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    public ListUserTodoUseCase(
            TodoRepository todoRepository,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.todoRepository = todoRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public List<Todo> execute(Long todolistId) {
        Long userId = authenticatedUserProvider.getUserId();
        return todoRepository.findAllByUserIdAndTodolistId(userId, todolistId);
    }
}