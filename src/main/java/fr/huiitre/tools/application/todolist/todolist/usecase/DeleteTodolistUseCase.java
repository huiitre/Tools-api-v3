package fr.huiitre.tools.application.todolist.todolist.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.todolist.todo.ports.TodoRepository;
import fr.huiitre.tools.application.todolist.todolist.ports.TodolistRepository;

@Service
@Transactional
public class DeleteTodolistUseCase implements SecuredUseCase {

    private final TodolistRepository todolistRepository;

    private final TodoRepository todoRepository;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.TODOLIST);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    public DeleteTodolistUseCase(
            TodolistRepository todolistRepository,
            TodoRepository todoRepository,
            AuthenticatedUserProvider authenticatedUserProvider) {
        this.todolistRepository = todolistRepository;
        this.todoRepository = todoRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public void execute(Long id) {
        Long userId = authenticatedUserProvider.getUserId();

        todoRepository.deleteByTodolistId(userId, id);
        todolistRepository.delete(userId, id);
    }
}