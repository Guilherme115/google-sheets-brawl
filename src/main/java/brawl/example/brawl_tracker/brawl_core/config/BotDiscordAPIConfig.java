package brawl.example.brawl_tracker.brawl_core.config;

import brawl.example.brawl_tracker.brawl_core.controller.BotController;
import jakarta.annotation.PostConstruct;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotDiscordAPIConfig {

    private final BotController botListener;

    @Value("${bot.discord.token}")
    private String token;

    public BotDiscordAPIConfig(BotController botListener) {
        this.botListener = botListener;
    }

    @PostConstruct
    public void startBot() throws Exception {
        JDA jda = JDABuilder.createDefault(token, GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.MESSAGE_CONTENT)
                .addEventListeners(botListener)
                .build();

        jda.updateCommands()
                .addCommands(
                        Commands.slash("infoteams", "Contribute registering the teams")
                )
                .queue();
    }
}
