package fr.huiitre.tools.config.health;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import fr.huiitre.tools.application.health.weight_log.ports.WeightLogRepository;
import fr.huiitre.tools.infrastructure.health.weight_log.PostgresWeightLogRepository;

@Configuration
public class HealthConfig {
    
    @Bean
    public WeightLogRepository weightLogRepository(
        JdbcTemplate jdbcTemplate
    ) {
        return new PostgresWeightLogRepository(
            jdbcTemplate
        );
    }
}
