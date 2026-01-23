package fr.huiitre.tools.config.dofus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

import fr.huiitre.tools.modules.dofus.almanax.application.ports.AlmanaxRepository;
import fr.huiitre.tools.modules.dofus.almanax.infrastructure.PostgresAlmanaxRepository;
import fr.huiitre.tools.modules.dofus.game.application.ports.GameServerRepository;
import fr.huiitre.tools.modules.dofus.game.application.ports.GameVersionRepository;
import fr.huiitre.tools.modules.dofus.game.infrastructure.PostgresGameServerRepository;
import fr.huiitre.tools.modules.dofus.game.infrastructure.PostgresGameVersionRepository;
import fr.huiitre.tools.modules.dofus.item.application.ports.ItemRepository;
import fr.huiitre.tools.modules.dofus.item.infrastructure.PostgresItemRepository;
import fr.huiitre.tools.modules.dofus.itemtype.application.ports.ItemTypeRepository;
import fr.huiitre.tools.modules.dofus.itemtype.infrastructure.PostgresItemTypeRepository;

@Configuration
public class DofusConfig {

        @Bean
        public GameVersionRepository gameVersionRepository(
                        JdbcTemplate jdbcTemplate) {
                return new PostgresGameVersionRepository(jdbcTemplate);
        }

        @Bean
        public GameServerRepository gameServerRepository(
                        JdbcTemplate jdbcTemplate) {
                return new PostgresGameServerRepository(
                                jdbcTemplate);
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
