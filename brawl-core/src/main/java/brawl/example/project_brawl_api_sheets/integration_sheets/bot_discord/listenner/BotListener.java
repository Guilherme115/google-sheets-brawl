package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.listenner;

import brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.commands.CommandManager;
import brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.service.RegistrationService;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Component
public class BotListener extends ListenerAdapter {

    private final CommandManager commandManager;
    private final RegistrationService registrationService;
    public BotListener(CommandManager commandManager, RegistrationService registrationService) {
        this.commandManager = commandManager;
        this.registrationService = registrationService;
    }

    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().updateCommands().addCommands(
                commandManager.getAllCommands().stream()
                        .map(cmd -> {
                            var commandData = Commands.slash(cmd.getName(), cmd.getDescription());

                            var options = cmd.getOptions();
                            if (options != null && !options.isEmpty()) {
                                commandData.addOptions(options);
                            }

                            return commandData;
                        })
                        .collect(Collectors.toList())
        ).queue();
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        commandManager.handle(event);
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("registration-modal")) {
            event.deferReply(true).queue();

            String teamName = event.getValue("team-name").getAsString();

            List<String> tags = new ArrayList<>();
            tags.add(event.getValue("player-1").getAsString());
            tags.add(event.getValue("player-2").getAsString());
            tags.add(event.getValue("player-3").getAsString());

            if (event.getValue("player-4") != null && !event.getValue("player-4").getAsString().isBlank()) {
                tags.add(event.getValue("player-4").getAsString());
            }

            String discordId = event.getUser().getId();
            String response = registrationService.processRegistration(discordId, teamName, tags);

            event.getHook().sendMessage(response).queue();
        }
    }


}