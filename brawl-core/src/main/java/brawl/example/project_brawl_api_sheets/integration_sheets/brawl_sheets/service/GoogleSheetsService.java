package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.TeamBattleResponseDTO;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Slf4j
@Service
public class GoogleSheetsService {

    private final Sheets sheets;
    private final Set<String> cache = new HashSet<>();

    @Getter
    @Value("${google.sheets.id}")
    private String spreadsheetId;

    public GoogleSheetsService(Sheets sheets) {
        this.sheets = sheets;
    }

    public void ensureHeaderExists() throws IOException {
        String range = "A1:Q1";
        ValueRange response = sheets.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values == null || values.isEmpty()) {
            log.info("Cabeçalho não encontrado na planilha. Criando...");
            List<List<Object>> headerData = new ArrayList<>();
            headerData.add(createHeader());

            ValueRange body = new ValueRange().setValues(headerData);
            this.sheets.spreadsheets().values()
                    .update(spreadsheetId, range, body)
                    .setValueInputOption("RAW")
                    .execute();
        }
    }

    public List<List<Object>> transformBattlesToSheetRows(List<TeamBattleResponseDTO> battleDtoList) {
        List<List<Object>> allRows = new ArrayList<>();

        for (TeamBattleResponseDTO team : battleDtoList) {
            for (BattleLogReceiveDTO.BattleLogInfo battle : team.getBattles()) {

                String uniqueKey = battle.getBattleTime();

                if (cache.contains(uniqueKey)) {
                    log.trace("Batalha com a chave '{}' já está no cache, pulando.", uniqueKey);
                    continue;
                }

                List<List<BattleLogReceiveDTO.Player>> teams = battle.getBattle().getTeams();
                List<BattleLogReceiveDTO.Player> aliados = teams.size() >= 1 ? teams.get(0) : Collections.emptyList();
                List<BattleLogReceiveDTO.Player> oponentes = teams.size() >= 2 ? teams.get(1) : Collections.emptyList();

                List<Object> row = new ArrayList<>();
                row.add(team.getTeamName());
                row.add(battle.getBattleTime());
                row.add(battle.getBattle().getMode());
                row.add(battle.getBattle().getResult());
                row.add(battle.getBattle().getDuration());

                fillPlayerAndBrawlerData(row, aliados, 3);
                fillPlayerAndBrawlerData(row, oponentes, 3);

                allRows.add(row);
                cache.add(uniqueKey);
            }
        }
        return allRows;
    }

    public void appendDataToSheet(List<List<Object>> data) throws IOException {
        String range = "A1";
        ValueRange body = new ValueRange().setValues(data);

        this.sheets.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    private List<Object> createHeader() {
        return Arrays.asList(
                "Team Name", "Battle Time", "Mode", "Result", "Duration",
                "Player #1", "Brawler #1", "Player #2", "Brawler #2", "Player #3", "Brawler #3",
                "Opponent #1", "Opponent Brawler #1", "Opponent #2", "Opponent Brawler #2", "Opponent #3", "Opponent Brawler #3"
        );
    }

    private void fillPlayerAndBrawlerData(List<Object> destinationRow, List<BattleLogReceiveDTO.Player> sourcePlayers, int expectedSize) {
        for (int i = 0; i < expectedSize; i++) {
            if (i < sourcePlayers.size()) {
                BattleLogReceiveDTO.Player player = sourcePlayers.get(i);
                destinationRow.add(Optional.ofNullable(player.getName()).orElse("N/A"));
                destinationRow.add(player.getBrawler() != null ? player.getBrawler().getName() : "N/A");
            } else {
                destinationRow.add("N/A");
                destinationRow.add("N/A");
            }
        }
    }
}