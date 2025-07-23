package brawl.example.project_brawl_api_sheets.integration_sheets.bot_discord.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerTagMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.PlayerTagRepository;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service.PlayerTagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegistrationService {
    private final PlayerTagRepository playerTagRepository;
    private final PlayerTagService tagService;


    public String processRegistration(String discordId, String teamName, List<String> playerTags) {

        if (playerTagRepository.existsByTeamName(teamName)) {
            return "O nome de time '" + teamName + "' já está registrado.";
        }

        for (String tag : playerTags) {
            if (!tagService.isPlayerTagisValid(tag)) {
                return "A tag '" + tag + "' é inválida.";
            }
            if (playerTagRepository.existsByTags(tag)) {
                String playerName = tagService.NameOfPlayer(tag);
                return "O jogador " + playerName + " (tag " + tag + ") já está registrado em outro time.";
            }
        }

        PlayerTagMODEL model = new PlayerTagMODEL();
        model.setTeamName(teamName);
        model.setTags(playerTags);
        model.setDiscordID(discordId);
        playerTagRepository.save(model);

        return "Time '" + teamName + "' registrado com sucesso! Obrigado por contribuir!";
    }
}