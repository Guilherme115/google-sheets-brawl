package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.config;

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
    private EventListener eventListener;

    @Bean
    public JDA jda() throws InterruptedException {
        JDA jda = JDABuilder.createDefault(key)
                .addEventListeners(eventListener)
                .build().awaitReady();
        jda.updateCommands().addCommands(
                Commands.slash("register", "iniciar o processo de cadastro"),
                Commands.slash("status-team", "Verifica o status de um time")
                        .addOption(OptionType.STRING, "team_name", "O nome do time que deseja ver o status", true)
        ).queue();

        return jda;
    }
}