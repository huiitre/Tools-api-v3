package fr.huiitre.tools.config.riot;

import fr.huiitre.tools.modules.riot.valorant.application.ports.RiotAuthPort;
import fr.huiitre.tools.modules.riot.valorant.application.ports.ValorantSkinRepository;
import fr.huiitre.tools.modules.riot.valorant.application.ports.ValorantUserSkinRepository;
import fr.huiitre.tools.modules.riot.valorant.application.ports.ValorantWatchlistRepository;
import fr.huiitre.tools.modules.riot.valorant.infrastructure.PostgresValorantSkinRepository;
import fr.huiitre.tools.modules.riot.valorant.infrastructure.PostgresValorantUserSkinRepository;
import fr.huiitre.tools.modules.riot.valorant.infrastructure.PostgresValorantWatchlistRepository;
import fr.huiitre.tools.modules.riot.valorant.infrastructure.RiotAuthHttpAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RiotConfig {

    @Bean
    public RiotAuthPort riotAuthPort() {
        return new RiotAuthHttpAdapter(new RestTemplate());
    }

    @Bean
    public ValorantSkinRepository valorantSkinRepository(JdbcTemplate jdbcTemplate) {
        return new PostgresValorantSkinRepository(jdbcTemplate);
    }

    @Bean
    public ValorantWatchlistRepository valorantWatchlistRepository(JdbcTemplate jdbcTemplate) {
        return new PostgresValorantWatchlistRepository(jdbcTemplate);
    }

    @Bean
    public ValorantUserSkinRepository valorantUserSkinRepository(JdbcTemplate jdbcTemplate) {
        return new PostgresValorantUserSkinRepository(jdbcTemplate);
    }
}
