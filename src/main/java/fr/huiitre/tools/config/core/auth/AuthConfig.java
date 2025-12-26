package fr.huiitre.tools.config.core.auth;

import javax.sql.DataSource;

import fr.huiitre.tools.application.core.auth.PasswordHasher;
import fr.huiitre.tools.application.core.auth.RegisterUserUseCase;
import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.application.core.user.ports.UserCredentialsRepository;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.infrastructure.persistence.core.user.PostgresUserAuthProviderRepository;
import fr.huiitre.tools.infrastructure.persistence.core.user.PostgresUserCredentialsRepository;
import fr.huiitre.tools.infrastructure.persistence.core.user.PostgresUserRepository;
import fr.huiitre.tools.infrastructure.security.password.BCryptPasswordHasher;
import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public UserRepository userRepository(DataSource dataSource) {
        return new PostgresUserRepository(dataSource);
    }

    @Bean
    public UserCredentialsRepository userCredentialsRepository(DataSource dataSource) {
        return new PostgresUserCredentialsRepository(dataSource);
    }

    @Bean
    public UserAuthProviderRepository userAuthProviderRepository(DataSource dataSource) {
        return new PostgresUserAuthProviderRepository(dataSource);
    }

    @Bean
    public PasswordHasher passwordHasher() {
        return new BCryptPasswordHasher();
    }

    @Bean
    public RegisterUserUseCase registerUserUseCase(
        UserRepository userRepository,
        UserCredentialsRepository userCredentialsRepository,
        UserAuthProviderRepository userAuthProviderRepository,
        UserRoleRepository userRoleRepository,
        RoleRepository roleRepository,
        PasswordHasher passwordHasher
    ) {
        return new RegisterUserUseCase(
            userRepository,
            userCredentialsRepository,
            userAuthProviderRepository,
            userRoleRepository,
            roleRepository,
            passwordHasher
        );
    }
}
