package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.*;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.BattleMatchRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SheetExportService {

    private final GoogleSheetsService googleSheetsService;
    private final BattleMatchRepository battleMatchRepository;
    private String lastProcessedBattleTime = "2020-01-01T00:00:00.000Z";

    @Async
    @Scheduled(initialDelay = 120000, fixedDelay = 300000)
    @Transactional
    public void exportNewBattlesToSheet() {
        log.info("--- INICIANDO EXPORTAÇÃO PARA GOOGLE SHEETS ---");
        try {
            googleSheetsService.ensureHeaderExists();

            List<BattleMatch> newBattles = battleMatchRepository.findByBattleTimeGreaterThanOrderByBattleTimeAsc(lastProcessedBattleTime);

            if (newBattles.isEmpty()) {
                log.info("Nenhuma batalha nova encontrada no banco para exportar.");
                return;
            }
            log.info("Encontradas {} novas batalhas para exportar para a planilha.", newBattles.size());

            List<List<Object>> sheetRows = transformBattlesToSheetRows(newBattles);
            googleSheetsService.appendDataToSheet(sheetRows);

            if (!newBattles.isEmpty()) {
                lastProcessedBattleTime = newBattles.get(newBattles.size() - 1).getBattleTime();
                log.info("Exportação para planilha concluída. Última batalha processada: {}", lastProcessedBattleTime);
            }

        } catch (IOException e) {
            log.error("Falha de IO ao comunicar com a API do Google Sheets.", e);
        } catch (Exception e) {
            log.error("Ocorreu um erro inesperado durante a exportação para a planilha.", e);
        }
    }

    private List<List<Object>> transformBattlesToSheetRows(List<BattleMatch> battles) {
        List<List<Object>> allRows = new ArrayList<>();
        for (BattleMatch battle : battles) {
            if (battle.getTeams() == null || battle.getTeams().size() < 2) {
                continue; // Pula partidas malformadas
            }

            // Ordena os times para garantir consistência:
            // 1. Times do tipo TRACKED vêm antes de UNKNOWN.
            // 2. Se ambos forem iguais, ordena por nome em ordem alfabética.
            battle.getTeams().sort(Comparator
                    .comparing(MatchTeamMODEL::getTeamType)
                    .thenComparing(MatchTeamMODEL::getNameTeam));

            MatchTeamMODEL teamA = battle.getTeams().get(0);
            MatchTeamMODEL teamB = battle.getTeams().get(1);

            List<Object> row = new ArrayList<>();

            // Adiciona os dados na ordem correta do novo cabeçalho
            row.add(teamA.getNameTeam());   // Equipe A (principal)
            row.add(teamB.getNameTeam());   // Equipe B (oponente)
            row.add(battle.getBattleTime());
            row.add(battle.getMode());
            row.add(battle.getResult());
            row.add(battle.getDuration());

            // Adiciona jogadores e brawlers da Equipe A
            fillPlayerAndBrawlerData(row, teamA.getPlayers());
            // Adiciona jogadores e brawlers da Equipe B
            fillPlayerAndBrawlerData(row, teamB.getPlayers());

            allRows.add(row);
        }
        return allRows;
    }

    private void fillPlayerAndBrawlerData(List<Object> destinationRow, List<PlayerPerformanceMODEL> performances) {
        // Ordena os jogadores por nome para manter a consistência na planilha
        performances.sort(Comparator.comparing(p -> p.getPlayer().getName()));

        for (int i = 0; i < 3; i++) {
            if (i < performances.size()) {
                PlayerPerformanceMODEL perf = performances.get(i);
                destinationRow.add(perf.getPlayer().getName());
                destinationRow.add("#" + perf.getPlayer().getTag());
                destinationRow.add(perf.getBrawler().getName());
            } else {
                destinationRow.add("N/A"); // Nome
                destinationRow.add("N/A"); // Tag
                destinationRow.add("N/A"); // Brawler
            }
        }
    }
}