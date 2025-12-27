package fr.huiitre.tools.config.core.module;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.huiitre.tools.application.core.module.ports.ModuleRepository;
import fr.huiitre.tools.infrastructure.persistence.core.module.PostgresModuleRepository;

@Configuration
public class ModuleConfig {
    
    @Bean
    public ModuleRepository moduleRepository(DataSource dataSource) {
        return new PostgresModuleRepository(dataSource);
    }
}
