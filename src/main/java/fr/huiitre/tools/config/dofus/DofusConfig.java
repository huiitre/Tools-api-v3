package fr.huiitre.tools.config.dofus;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
            DataSource dataSource) {
        return new PostgresGameVersionRepository(dataSource);
    }

    @Bean
    public ItemTypeRepository itemTypeRepository(
            DataSource dataSource) {
        return new PostgresItemTypeRepository(
                dataSource);
    }

    @Bean
    public ItemRepository itemRepository(
            DataSource dataSource) {
        return new PostgresItemRepository(
                dataSource);
    }

    @Bean
    public AlmanaxRepository almanaxRepository(
            DataSource dataSource) {
        return new PostgresAlmanaxRepository(
                dataSource);
    }
}
