package ru.practicum.main.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.statsclient.StatsClientService;

@Configuration
public class StatsClientServiceConfig {

    @Bean
    public StatsClientService getStatsClient() {
        return new StatsClientService();
    }
}
