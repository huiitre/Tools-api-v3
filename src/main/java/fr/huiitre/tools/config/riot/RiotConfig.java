package fr.huiitre.tools.config.riot;

import fr.huiitre.tools.modules.riot.valorant.application.ports.RiotAuthPort;
import fr.huiitre.tools.modules.riot.valorant.infrastructure.RiotAuthHttpAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RiotConfig {

    @Bean
    public RiotAuthPort riotAuthPort() {
        return new RiotAuthHttpAdapter(new RestTemplate());
    }
}
