package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.amqp.core.Queue;


@Configuration
public class RabbitConfig {
    @Bean
    public Queue pedidosQueue() {
        return new Queue("brawl.analysis.pedidos", false);
    }

    @Bean
    public Queue resultadosQueue() {
        return new Queue("brawl.analysis.resultados", false);
    }
}

