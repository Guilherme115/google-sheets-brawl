package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.TeamBattleResponseDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.TeamWithPlayersRelationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SyncService {

    private final GoogleSheetsService googleSheetsService;
    private final BrawlService brawlService;
    private final TeamService teamService;
    private final BrawlDataService brawlDataService;

    @Autowired
    public SyncService(TeamService teamService,
                       BrawlService brawlService,
                       GoogleSheetsService googleSheetsService,
                       BrawlDataService brawlDataService) {
        this.teamService = teamService;
        this.brawlService = brawlService;
        this.googleSheetsService = googleSheetsService;
        this.brawlDataService = brawlDataService;
    }

    @Scheduled(fixedRate = 600000)
    public void sync() {
        log.info("------------------- INICIANDO ROTINA DE SINCRONIZAÇÃO -------------------");
        try {
            googleSheetsService.ensureHeaderExists();

            List<TeamWithPlayersRelationDTO> allTeamsFromDb = teamService.getPlayersTagsAndNameTeam();
            if (allTeamsFromDb.isEmpty()) {
                log.warn("Nenhuma equipe encontrada no banco de dados para sincronizar. Rotina encerrada.");
                return;
            }
            log.info("Encontradas {} equipes no banco de dados.", allTeamsFromDb.size());

            for (TeamWithPlayersRelationDTO team : allTeamsFromDb) {
                if (team == null || team.getPlayersTags() == null || team.getPlayersTags().isEmpty()) continue;

                String mainTag = team.getPlayersTags().get(0);
                String teamName = team.getTeamName();
                log.info("Processando equipe '{}' usando a tag '{}'.", teamName, mainTag);

                BattleLogReceiveDTO fullBattleLog = brawlService.fetchPlayerBattleLog(mainTag);

                if (fullBattleLog == null || fullBattleLog.getItems() == null || fullBattleLog.getItems().isEmpty()) {
                    log.info("Nenhum log de batalha encontrado para a equipe '{}'.", teamName);
                    continue;
                }

                log.info("Salvando {} batalhas no banco de dados para a equipe '{}'.", fullBattleLog.getItems().size(), teamName);

                // CORREÇÃO 2: Passamos o objeto 'team' inteiro, não apenas a 'mainTag'
                brawlDataService.processAndSaveBattleLog(fullBattleLog, team);

                List<BattleLogReceiveDTO.BattleLogInfo> filteredBattles = brawlService.filterTeamBattles(fullBattleLog, team.getPlayersTags());

                if (filteredBattles.isEmpty()) {
                    log.info("Nenhuma partida da equipe '{}' correspondeu aos filtros para a planilha.", teamName);
                    continue;
                }

                TeamBattleResponseDTO teamForSheet = new TeamBattleResponseDTO(teamName, filteredBattles);
                List<List<Object>> sheetRows = googleSheetsService.transformBattlesToSheetRows(Collections.singletonList(teamForSheet));

                if (!sheetRows.isEmpty()) {
                    log.info("Preparando para enviar {} novas linhas da equipe '{}' para a planilha...", sheetRows.size(), teamName);
                    googleSheetsService.appendDataToSheet(sheetRows);
                    log.info("Dados da equipe '{}' enviados para a planilha.", teamName);
                }
            }

            log.info("SINCRONIZAÇÃO CONCLUÍDA!");

        } catch (IOException e) {
            log.error("Falha de IO ao comunicar com a API do Google Sheets.", e);
        } catch (Exception e) {
            log.error("Ocorreu um erro inesperado durante a sincronização.", e);
        }
        log.info("------------------- FIM DA ROTINA DE SINCRONIZAÇÃO -------------------");
    }
}