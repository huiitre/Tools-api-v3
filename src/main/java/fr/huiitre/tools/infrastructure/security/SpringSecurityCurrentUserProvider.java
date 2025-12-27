package fr.huiitre.tools.infrastructure.security;


import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import fr.huiitre.tools.application.common.security.ports.CurrentUserProvider;

public class SpringSecurityCurrentUserProvider implements CurrentUserProvider {

    @Override
    public String getCurrentUserId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }

        return authentication.getName(); // JWT sub
    }
}
