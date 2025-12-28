package fr.huiitre.tools.config.health;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import fr.huiitre.tools.application.health.weight_log.ports.WeightLogRepository;
import fr.huiitre.tools.infrastructure.persistence.health.weight_log.PostgresWeightLogRepository;

@Configuration
public class HealthConfig {
    
    @Bean
    public WeightLogRepository weightLogRepository(
        DataSource dataSource
    ) {
        return new PostgresWeightLogRepository(
            dataSource
        );
    }
}
