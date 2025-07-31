package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamRegisterMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SyncService {

    private final BrawlService brawlService;
    private final TeamRepository teamRegisterRepository;
    private final BrawlDataService brawlDataService;

    @Async
    @Scheduled(initialDelay = 60000, fixedRate = 600000)
    public void syncBattleLogs() {
        log.info("------------------- INICIANDO ROTINA DE SINCRONIZAÇÃO DE BATALHAS -------------------");

        List<TeamRegisterMODEL> allTeamsFromDb = teamRegisterRepository.findAll();
        if (allTeamsFromDb.isEmpty()) {
            log.warn("Nenhuma equipe encontrada no banco de dados para sincronizar.");
            return;
        }
        log.info("Encontradas {} equipes para processar.", allTeamsFromDb.size());

        // PASSO 1: Criar o "mapa de conhecimento" com todos os jogadores e seus times.
        Map<String, TeamRegisterMODEL> playerTagToTeamMap = new HashMap<>();
        for (TeamRegisterMODEL team : allTeamsFromDb) {
            for (PlayerMODEL player : team.getPlayers()) {
                playerTagToTeamMap.put(player.getTag(), team);
            }
        }

        Set<String> processedBattleTimesInThisRun = new HashSet<>();

        for (TeamRegisterMODEL team : allTeamsFromDb) {
            if (team.getPlayers() == null || team.getPlayers().isEmpty()) continue;

            String mainTag = team.getPlayers().iterator().next().getTag();
            log.info("Processando equipe '{}' usando a tag '{}'.", team.getName(), mainTag);

            BattleLogReceiveDTO rawBattleLog = brawlService.fetchPlayerBattleLog(mainTag);
            if (rawBattleLog == null || rawBattleLog.getItems() == null || rawBattleLog.getItems().isEmpty()) {
                log.info("Nenhum log de batalha retornado pela API para a equipe '{}'.", team.getName());
                continue;
            }

            List<String> teamTags = team.getPlayers().stream().map(PlayerMODEL::getTag).collect(Collectors.toList());
            List<BattleLogReceiveDTO.BattleLogInfo> filteredBattles = brawlService.getFilteredBattles(rawBattleLog, teamTags);

            List<BattleLogReceiveDTO.BattleLogInfo> trulyNewBattles = filteredBattles.stream()
                    .filter(info -> !processedBattleTimesInThisRun.contains(info.getBattleTime()))
                    .collect(Collectors.toList());

            if (trulyNewBattles.isEmpty()) {
                log.info("Nenhuma batalha nova para a equipe '{}' nesta rodada.", team.getName());
                continue;
            }

            trulyNewBattles.forEach(info -> processedBattleTimesInThisRun.add(info.getBattleTime()));

            BattleLogReceiveDTO battleLogToSave = new BattleLogReceiveDTO();
            battleLogToSave.setItems(trulyNewBattles);

            // PASSO 2: Enviar o log para salvar junto com o "mapa de conhecimento".
            brawlDataService.processAndSaveBattleLog(battleLogToSave, playerTagToTeamMap);
        }
        log.info("------------------- FIM DA ROTINA DE SINCRONIZAÇÃO DE BATALHAS -------------------");
    }
}