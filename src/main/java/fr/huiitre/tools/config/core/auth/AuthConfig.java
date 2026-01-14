package fr.huiitre.tools.config.core.auth;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

import fr.huiitre.tools.application.core.auth.EmailSender;
import fr.huiitre.tools.application.core.auth.PasswordHasher;
import fr.huiitre.tools.application.core.auth.RegisterUserUseCase;
import fr.huiitre.tools.application.core.auth.UserEmailVerificationRepository;
import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;
import fr.huiitre.tools.application.core.user.ports.UserAuthProviderRepository;
import fr.huiitre.tools.application.core.user.ports.UserCredentialsRepository;
import fr.huiitre.tools.application.core.user.ports.UserRepository;
import fr.huiitre.tools.infrastructure.auth.mail.AuthMailSenderService;
import fr.huiitre.tools.infrastructure.core.user.PostgresUserAuthProviderRepository;
import fr.huiitre.tools.infrastructure.core.user.PostgresUserCredentialsRepository;
import fr.huiitre.tools.infrastructure.core.user.PostgresUserEmailVerificationRepository;
import fr.huiitre.tools.infrastructure.core.user.PostgresUserRepository;
import fr.huiitre.tools.infrastructure.security.password.BCryptPasswordHasher;

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
            PasswordHasher passwordHasher) {
        return new RegisterUserUseCase(
                userRepository,
                userCredentialsRepository,
                userAuthProviderRepository,
                userRoleRepository,
                roleRepository,
                passwordHasher);
    }

    @Bean
    public UserEmailVerificationRepository userEmailVerificationRepository(DataSource dataSource) {
        return new PostgresUserEmailVerificationRepository(dataSource);
    }

    @Bean
    public EmailSender emailSender(
        JavaMailSender mailSender
    ) {
        return new AuthMailSenderService(mailSender);
    }
}
