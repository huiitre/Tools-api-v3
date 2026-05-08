package fr.huiitre.tools.config.riot;

import fr.huiitre.tools.modules.riot.valorant.sync.application.ValorantSkinDataProvider;
import fr.huiitre.tools.modules.riot.valorant.sync.application.ValorantSkinSyncRepository;
import fr.huiitre.tools.modules.riot.valorant.sync.infrastructure.PostgresValorantSkinSyncRepository;
import fr.huiitre.tools.modules.riot.valorant.sync.infrastructure.ValorantApiSkinDataProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RiotSyncConfig {

    @Bean
    public ValorantSkinDataProvider valorantSkinDataProvider() {
        return new ValorantApiSkinDataProvider(new RestTemplate());
    }

    @Bean
    public ValorantSkinSyncRepository valorantSkinSyncRepository(JdbcTemplate jdbcTemplate) {
        return new PostgresValorantSkinSyncRepository(jdbcTemplate);
    }
}
