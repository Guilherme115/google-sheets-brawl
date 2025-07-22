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
    private final RegistrationService registrationService; // Injetar o serviço

    public BotListener(CommandManager commandManager, RegistrationService registrationService) {
        this.commandManager = commandManager;
        this.registrationService = registrationService;
    }

    // Registra os comandos no Discord quando o bot fica online
    @Override
    public void onReady(ReadyEvent event) {
        event.getJDA().updateCommands().addCommands(
                commandManager.getAllCommands().stream()
                        .map(cmd -> {
                            // Cria o comando básico
                            var commandData = Commands.slash(cmd.getName(), cmd.getDescription());

                            // Adiciona as opções que o comando possa ter
                            var options = cmd.getOptions(); // Supondo que este método exista
                            if (options != null && !options.isEmpty()) {
                                commandData.addOptions(options);
                            }

                            return commandData;
                        })
                        .collect(Collectors.toList())
        ).queue();
    }

    // Delega a execução para o CommandManager
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        commandManager.handle(event);
    }

    // Lida com a submissão do nosso Modal de registro
    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (event.getModalId().equals("registration-modal")) {
            event.deferReply(true).queue(); // Responde de forma "ephemeral" (só o usuário vê)

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

            // Envia o resultado final para o usuário
            event.getHook().sendMessage(response).queue();
        }
    }


}