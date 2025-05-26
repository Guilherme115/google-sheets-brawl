package brawl.example.project_brawl_api_sheets.config;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BotDiscordAPI {

@Value("${bot.discord.key}")
 private String token;


@Bean
 public JDA jda () {
    try {
        return JDABuilder.createDefault(token)
                .build()
                .awaitReady();
    } catch (InterruptedException e) {
        throw new RuntimeException(e);
    }
}

}
