package fr.huiitre.tools.application.todolist.todolist.usecase;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import fr.huiitre.tools.application.common.security.ports.AuthenticatedUserProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.application.todolist.todolist.ports.TodolistRepository;
import fr.huiitre.tools.domain.todolist.todolist.Todolist;

@Service
@Transactional
public class ListUserTodolistsUseCase implements SecuredUseCase {

    private final TodolistRepository todolistRepository;

    private final AuthenticatedUserProvider authenticatedUserProvider;

    @Override
    public Optional<ModuleCode> requiredModule() {
        return Optional.of(ModuleCode.TOOLS_TODOLIST);
    }

    @Override
    public RoleCode requiredRole() {
        return RoleCode.USER;
    }

    public ListUserTodolistsUseCase(
        TodolistRepository todolistRepository,
        AuthenticatedUserProvider authenticatedUserProvider
    ) {
        this.todolistRepository = todolistRepository;
        this.authenticatedUserProvider = authenticatedUserProvider;
    }

    public List<Todolist> execute() {
        Long userId = authenticatedUserProvider.getUserId();
        List<Todolist> todolists = todolistRepository.findAllByUserId(userId);
        return todolists;
    }
}