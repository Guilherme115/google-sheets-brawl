package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerTagMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service.PlayerTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final PlayerTagService tagService;


    public String processRegistration(String discordId, String teamName, List<String> playerTags) {



        for (String tag : playerTags) {
            if (!tagService.isPlayerTagisValid(tag)) {
                return "A tag '" + tag + "' é inválida.";
            }
                String playerName = tagService.NameOfPlayer(tag);
                return "O jogador " + playerName + " (tag " + tag + ") já está registrado em outro time.";

        }

        PlayerTagMODEL model = new PlayerTagMODEL();
        model.setTeamName(teamName);
        model.setTags(playerTags);
        model.setDiscordID(discordId);

        return "Time '" + teamName + "' registrado com sucesso! Obrigado por contribuir!";
    }
}