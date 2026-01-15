package fr.huiitre.tools.config.dofus;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import fr.huiitre.tools.application.dofus.ports.repositories.AlmanaxRepository;
import fr.huiitre.tools.application.dofus.ports.repositories.GameVersionRepository;
import fr.huiitre.tools.application.dofus.ports.repositories.ItemRepository;
import fr.huiitre.tools.application.dofus.ports.repositories.ItemTypeRepository;
import fr.huiitre.tools.infrastructure.dofus.persistence.PostgresAlmanaxRepository;
import fr.huiitre.tools.infrastructure.dofus.persistence.PostgresGameVersionRepository;
import fr.huiitre.tools.infrastructure.dofus.persistence.PostgresItemRepository;
import fr.huiitre.tools.infrastructure.dofus.persistence.PostgresItemTypeRepository;

@Configuration
public class DofusConfig {

    @Bean
    public GameVersionRepository gameVersionRepository(
            JdbcTemplate jdbcTemplate) {
        return new PostgresGameVersionRepository(jdbcTemplate);
    }

    @Bean
    public ItemTypeRepository itemTypeRepository(
            JdbcTemplate jdbcTemplate) {
        return new PostgresItemTypeRepository(
                jdbcTemplate);
    }

    @Bean
    public ItemRepository itemRepository(
            JdbcTemplate jdbcTemplate) {
        return new PostgresItemRepository(
                jdbcTemplate);
    }

    @Bean
    public AlmanaxRepository almanaxRepository(
            JdbcTemplate jdbcTemplate) {
        return new PostgresAlmanaxRepository(
                jdbcTemplate);
    }
}
