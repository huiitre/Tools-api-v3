package fr.huiitre.tools.config.core.role;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;
import fr.huiitre.tools.infrastructure.core.role.PostgresRoleRepository;
import fr.huiitre.tools.infrastructure.core.role.PostgresUserRoleRepository;

@Configuration
public class RoleConfig {

    @Bean
    public RoleRepository roleRepository(JdbcTemplate jdbcTemplate) {
        return new PostgresRoleRepository(jdbcTemplate);
    }

    @Bean
    public UserRoleRepository userRoleRepository(JdbcTemplate jdbcTemplate) {
        return new PostgresUserRoleRepository(jdbcTemplate);
    }

}
