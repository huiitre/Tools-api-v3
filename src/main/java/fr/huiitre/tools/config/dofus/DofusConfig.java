package fr.huiitre.tools.config.dofus;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import fr.huiitre.tools.modules.dofus.almanax.application.ports.AlmanaxRepository;
import fr.huiitre.tools.modules.dofus.almanax.infrastructure.PostgresAlmanaxRepository;
import fr.huiitre.tools.modules.dofus.catalogue.application.ports.CatalogueItemRepository;
import fr.huiitre.tools.modules.dofus.catalogue.infrastructure.PostgresCatalogueItemRepository;
import fr.huiitre.tools.modules.dofus.game.application.ports.GameServerRepository;
import fr.huiitre.tools.modules.dofus.game.application.ports.GameVersionRepository;
import fr.huiitre.tools.modules.dofus.game.infrastructure.PostgresGameServerRepository;
import fr.huiitre.tools.modules.dofus.game.infrastructure.PostgresGameVersionRepository;
import fr.huiitre.tools.modules.dofus.item.application.ports.ItemRepository;
import fr.huiitre.tools.modules.dofus.item.infrastructure.PostgresItemRepository;
import fr.huiitre.tools.modules.dofus.itemtype.application.ports.ItemTypeRepository;
import fr.huiitre.tools.modules.dofus.itemtype.infrastructure.PostgresItemTypeRepository;
import fr.huiitre.tools.modules.dofus.pricing.application.ports.ItemPriceRepository;
import fr.huiitre.tools.modules.dofus.pricing.infrastructure.PostgresItemPriceRepository;
import fr.huiitre.tools.modules.dofus.recipe.application.ports.RecipeRepository;
import fr.huiitre.tools.modules.dofus.recipe.infrastructure.PostgresRecipeRepository;

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

        @Bean
        public ItemPriceRepository itemPriceRepository(
                        NamedParameterJdbcTemplate jdbcTemplate) {
                return new PostgresItemPriceRepository(
                                jdbcTemplate);
        }

        @Bean
        public CatalogueItemRepository catalogueItemRepository(
                        NamedParameterJdbcTemplate jdbcTemplate) {
                return new PostgresCatalogueItemRepository(
                                jdbcTemplate);
        }

        @Bean
        public RecipeRepository recipeRepository(
                        JdbcTemplate jdbcTemplate) {
                return new PostgresRecipeRepository(
                                jdbcTemplate);
        }
}
