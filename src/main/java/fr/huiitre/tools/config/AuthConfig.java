package fr.huiitre.tools.config;

import javax.sql.DataSource;

import fr.huiitre.tools.application.auth.PasswordHasher;
import fr.huiitre.tools.application.auth.RegisterUserUseCase;
import fr.huiitre.tools.application.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.application.user.ports.UserCredentialsRepository;
import fr.huiitre.tools.application.user.ports.UserRepository;
import fr.huiitre.tools.infrastructure.persistence.user.PostgresUserAuthProviderRepository;
import fr.huiitre.tools.infrastructure.persistence.user.PostgresUserCredentialsRepository;
import fr.huiitre.tools.infrastructure.persistence.user.PostgresUserRepository;
import fr.huiitre.tools.infrastructure.security.password.BCryptPasswordHasher;

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
        PasswordHasher passwordHasher
    ) {
        return new RegisterUserUseCase(
            userRepository,
            userCredentialsRepository,
            userAuthProviderRepository,
            passwordHasher
        );
    }
}
