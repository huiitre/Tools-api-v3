package fr.huiitre.tools.config.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.huiitre.tools.application.common.security.ports.CurrentUserProvider;
import fr.huiitre.tools.application.common.security.ports.ModuleAuthorizationPort;
import fr.huiitre.tools.application.common.security.ports.UserRoleProvider;
// import fr.huiitre.tools.infrastructure.security.FakeModuleAuthorizationAdapter;
import fr.huiitre.tools.infrastructure.security.PostgresModuleAuthorizationAdapter;
import fr.huiitre.tools.infrastructure.security.PostgresUserRoleProvider;
import fr.huiitre.tools.infrastructure.security.SpringSecurityCurrentUserProvider;
import fr.huiitre.tools.infrastructure.security.aop.UseCaseAuthorizationAspect;

@Configuration
public class AuthorizationConfig {

    @Bean
    public ModuleAuthorizationPort moduleAuthorizationPort(DataSource dataSource) {
        // return new FakeModuleAuthorizationAdapter();
        return new PostgresModuleAuthorizationAdapter(dataSource);
    }

    @Bean
    public CurrentUserProvider currentUserProvider() {
        return new SpringSecurityCurrentUserProvider();
    }

    @Bean
    public UseCaseAuthorizationAspect useCaseAuthorizationAspect(
        ModuleAuthorizationPort moduleAuthorizationPort,
        UserRoleProvider userRoleProvider,
        CurrentUserProvider currentUserProvider
    ) {
        return new UseCaseAuthorizationAspect(
            moduleAuthorizationPort,
            userRoleProvider,
            currentUserProvider
        );
    }

    @Bean
    public UserRoleProvider userRoleProvider(DataSource dataSource) {
        return new PostgresUserRoleProvider(dataSource);
    }
}