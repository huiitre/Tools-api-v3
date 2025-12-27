package fr.huiitre.tools.infrastructure.security.aop;

import java.util.Optional;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import fr.huiitre.tools.application.common.security.exception.ForbiddenException;
import fr.huiitre.tools.application.common.security.ports.CurrentUserProvider;
import fr.huiitre.tools.application.common.security.ports.ModuleAuthorizationPort;
import fr.huiitre.tools.application.common.security.ports.UserRoleProvider;
import fr.huiitre.tools.application.common.security.usecase.SecuredUseCase;
import fr.huiitre.tools.application.core.module.ModuleCode;
import fr.huiitre.tools.application.core.role.RoleCode;
import fr.huiitre.tools.infrastructure.security.RoleHierarchy;

@Aspect
public class UseCaseAuthorizationAspect {

    private final ModuleAuthorizationPort moduleAuthorizationPort;
    private final UserRoleProvider userRoleProvider;
    private final CurrentUserProvider currentUserProvider;

    public UseCaseAuthorizationAspect(
            ModuleAuthorizationPort moduleAuthorizationPort,
            UserRoleProvider userRoleProvider,
            CurrentUserProvider currentUserProvider
    ) {
        this.moduleAuthorizationPort = moduleAuthorizationPort;
        this.userRoleProvider = userRoleProvider;
        this.currentUserProvider = currentUserProvider;
    }

    @Around("execution(* *.execute(..)) && this(securedUseCase)")
    public Object authorize(
            ProceedingJoinPoint pjp,
            SecuredUseCase securedUseCase
    ) throws Throwable {

        String userId = currentUserProvider.getCurrentUserId();

        if (userId == null || "anonymousUser".equals(userId)) {
            return pjp.proceed();
        }

        Optional<ModuleCode> moduleOpt = securedUseCase.requiredModule();
        RoleCode requiredRole = securedUseCase.requiredRole();

        // 1) Vérification accès module (si module présent)
        if (moduleOpt.isPresent()) {
            if (!moduleAuthorizationPort.hasAccess(userId, moduleOpt.get())) {
                throw new ForbiddenException(moduleOpt.get());
            }
        }

        // 2) Récupération rôle effectif
        RoleCode actualRole =
                userRoleProvider.getUserRole(userId, moduleOpt.orElse(null));

        // 3) Comparaison hiérarchique
        if (!RoleHierarchy.hasAtLeast(actualRole, requiredRole)) {
            throw new ForbiddenException(
                "Required role " + requiredRole + ", but was " + actualRole
            );
        }

        return pjp.proceed();
    }
}
