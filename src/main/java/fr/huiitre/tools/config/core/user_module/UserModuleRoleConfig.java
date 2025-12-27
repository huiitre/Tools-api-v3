package fr.huiitre.tools.config.core.user_module;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.infrastructure.persistence.core.user_module.PostgresUserModuleRoleRepository;

@Configuration
public class UserModuleRoleConfig {
    
    @Bean
    public UserModuleRoleRepository userModuleRoleRepository(DataSource dataSource) {
        return new PostgresUserModuleRoleRepository(dataSource);
    }
}
