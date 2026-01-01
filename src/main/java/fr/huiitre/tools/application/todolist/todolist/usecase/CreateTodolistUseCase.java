package fr.huiitre.tools.application.todolist.todolist.usecase;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.todolist.todolist.command.CreateTodolistCommand;
import fr.huiitre.tools.application.todolist.todolist.ports.TodolistRepository;
import fr.huiitre.tools.domain.todolist.todolist.Todolist;

@Service
@Transactional
public class CreateTodolistUseCase implements SecuredUseCase {

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.TOOLS_TODOLIST);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    private final TodolistRepository todolistRepository;

    public CreateTodolistUseCase(
        AuthenticatedUserProvider authenticatedUserProvider,
        TodolistRepository todolistRepository
    ) {
        this.authenticatedUserProvider = authenticatedUserProvider;
        this.todolistRepository = todolistRepository;
    }

    public void execute(CreateTodolistCommand command) {
        Long userId = authenticatedUserProvider.getUserId();

        Todolist todolist = Todolist.create(
            command.getName(),
            command.isActive(),
            command.isFavorite(),
            command.getColorHex(),
            command.getDisplayOrder()
        );
        todolistRepository.save(userId, todolist);
    }
}