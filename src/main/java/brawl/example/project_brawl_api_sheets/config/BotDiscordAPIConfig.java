package brawl.example.project_brawl_api_sheets.config;

import brawl.example.project_brawl_api_sheets.controller.BotController;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotDiscordAPIConfig {
private BotController botListener;

@Value("${bot.discord.key}")
private String token;



    @PostConstruct
    public void startBot() throws Exception {
        JDABuilder.createDefault(token)
                .addEventListeners(botListener)
                .build();
    }
}

git
