package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.config;

import brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.listenner.BotListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.EventListener;

@Configuration
public class BotDiscordAPIConfig {

    @Value("${discord.api.key}")
    private String key;

    @Autowired
    private BotListener botListener;

    @Bean
    public JDA jda() {
        return JDABuilder.createDefault(key)
                .addEventListeners(botListener)
                .build();
    }
}