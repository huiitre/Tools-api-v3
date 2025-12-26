package fr.huiitre.tools.config.core.role;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.huiitre.tools.application.core.role.ports.RoleRepository;
import fr.huiitre.tools.application.core.role.ports.UserRoleRepository;
import fr.huiitre.tools.infrastructure.persistence.core.role.PostgresRoleRepository;
import fr.huiitre.tools.infrastructure.persistence.core.role.PostgresUserRoleRepository;

@Configuration
public class RoleConfig {
    
    @Bean
    public RoleRepository roleRepository(DataSource dataSource) {
        return new PostgresRoleRepository(dataSource);
    }

    @Bean
    public UserRoleRepository userRoleRepository(DataSource dataSource) {
        return new PostgresUserRoleRepository(dataSource);
    }

}
