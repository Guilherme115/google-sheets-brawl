package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.listenner;

import brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.service.ActionsBotService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BotController extends ListenerAdapter {

    private final ActionsBotService actionsBotService;

    // Construtor para injeção de dependência (melhor prática)
    public BotController(ActionsBotService actionsBotService) {
        this.actionsBotService = actionsBotService;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String command = event.getName();

        // Lógica para o comando /register
        if (command.equals("register")) {
            String userID = event.getUser().getId();
            String resposta = actionsBotService.inicializateFlow(userID);
            event.reply(resposta).queue();

        } else if (command.equals("status-team")) {
            event.deferReply().queue();
            String teamName = Objects.requireNonNull(event.getOption("team_name")).getAsString();
            actionsBotService.getTeamStatus(event, teamName);
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String userID = event.getAuthor().getId();
        String msg = event.getMessage().getContentRaw();
        String resposta = actionsBotService.mainFlow(userID, msg);

        if (resposta != null && !resposta.isEmpty()) {
            event.getChannel().sendMessage(resposta).queue();
        }
    }
}