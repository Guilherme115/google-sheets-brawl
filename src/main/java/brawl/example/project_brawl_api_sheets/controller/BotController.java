package brawl.example.project_brawl_api_sheets.controller;

import brawl.example.project_brawl_api_sheets.config.BotDiscordAPIConfig;
import brawl.example.project_brawl_api_sheets.service.ActionsBotService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class BotController extends ListenerAdapter {
    private ActionsBotService service;


    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String userID = event.getUser().getId();
        String resposta = service.inicializateFlow(userID);
        event.reply(resposta).queue();
    }

        @Override
        public void onMessageReceived(MessageReceivedEvent event) {
        String userID = event.getAuthor().getId();
        String msg = event.getMessage().getContentRaw();
        if (event.getAuthor().isBot()) return;
        String resposta = service.mainFlow(userID,msg);
        event.getChannel().sendMessage(resposta).queue();

    }







}
