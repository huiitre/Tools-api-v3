package fr.huiitre.tools.config.core.user_module;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import fr.huiitre.tools.application.core.user_module.ports.UserModuleRoleRepository;
import fr.huiitre.tools.infrastructure.core.user_module.PostgresUserModuleRoleRepository;

@Configuration
public class UserModuleRoleConfig {

    @Bean
    public UserModuleRoleRepository userModuleRoleRepository(JdbcTemplate jdbcTemplate) {
        return new PostgresUserModuleRoleRepository(jdbcTemplate);
    }
}
