package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.*;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.MatchSetRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SheetExportService {

    private final GoogleSheetsService googleSheetsService;
    private final MatchSetRepository matchSetRepository;
    private LocalDateTime lastProcessedSetTime = LocalDateTime.parse("2020-01-01T00:00:00");

    @Async
    @Scheduled(initialDelay = 120000, fixedDelay = 300000)
    @Transactional
    public void exportNewSetsToSheet() {
        log.info("--- INICIANDO EXPORTAÇÃO DE SETS PARA GOOGLE SHEETS ---");
        try {
            googleSheetsService.ensureHeaderExists();

            List<MatchSet> newSets = matchSetRepository.findBySetStartTimeGreaterThanOrderBySetStartTimeAsc(lastProcessedSetTime);

            if (newSets.isEmpty()) {
                log.info("Nenhum novo set encontrado no banco para exportar.");
                return;
            }
            log.info("Encontrados {} novos sets para exportar para a planilha.", newSets.size());

            List<List<Object>> sheetRows = transformSetsToSheetRows(newSets);
            googleSheetsService.appendDataToSheet(sheetRows);

            lastProcessedSetTime = newSets.get(newSets.size() - 1).getSetStartTime();
            log.info("Exportação de {} sets para planilha concluída. Último set processado: {}", sheetRows.size(), lastProcessedSetTime);

        } catch (IOException e) {
            log.warn("Falha de IO ao comunicar com a API do Google Sheets: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Ocorreu um erro inesperado durante a exportação para a planilha.", e);
        }
    }

    private List<List<Object>> transformSetsToSheetRows(List<MatchSet> sets) {
        List<List<Object>> allRows = new ArrayList<>();
        for (MatchSet set : sets) {
            if (set.getBattles() == null || set.getBattles().isEmpty()) continue;

            // Pega a primeira partida do set como referência para os detalhes
            BattleMatch firstBattle = set.getBattles().stream()
                    .min(Comparator.comparing(BattleMatch::getBattleTime))
                    .orElse(null);

            if (firstBattle == null || firstBattle.getTeams().size() < 2) continue;

            // Ordena os times da primeira partida para garantir consistência
            firstBattle.getTeams().sort(Comparator.comparing(MatchTeamMODEL::getTeamType).thenComparing(MatchTeamMODEL::getNameTeam));
            MatchTeamMODEL teamA = firstBattle.getTeams().get(0);
            MatchTeamMODEL teamB = firstBattle.getTeams().get(1);

            List<Object> row = new ArrayList<>();

            // Preenche as colunas de resumo do Set
            row.add(teamA.getNameTeam()); // Team Name
            row.add(teamB.getNameTeam()); // Opponent Name
            row.add(set.getSetStartTime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))); // Battle Time (do set)
            row.add(firstBattle.getMode()); // Mode (do primeiro jogo)

            // Lógica para o Result (victory/defeat) baseado no vencedor do SET
            String finalResult = "draw";
            if (set.getWinningTeamName().equalsIgnoreCase(teamA.getNameTeam())) {
                finalResult = "victory";
            } else if (set.getWinningTeamName().equalsIgnoreCase(teamB.getNameTeam())) {
                finalResult = "defeat";
            }
            row.add(finalResult);

            row.add(firstBattle.getDuration()); // Duration (do primeiro jogo)
            row.add(set.getFinalResult()); // Scoreboard (ex: "2-1")

            // Preenche os dados dos jogadores e brawlers (do primeiro jogo)
            fillPlayerAndBrawlerData(row, teamA.getPlayers());
            fillPlayerAndBrawlerData(row, teamB.getPlayers());

            allRows.add(row);
        }
        return allRows;
    }

    private void fillPlayerAndBrawlerData(List<Object> destinationRow, List<PlayerPerformanceMODEL> performances) {
        // Ordena para garantir consistência (Player #1, #2, #3)
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