package brawl.example.brawl_tracker.service;

import brawl.example.brawl_tracker.dto.TeamBattleDTO;
import brawl.example.brawl_tracker.entity.PlayerTagData;
import brawl.example.brawl_tracker.model.BrawlRequestMODEL;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GoogleSheetsService {

    private final Sheets sheets;
    private final PlayerTagData repository;
    private final Set<String> cache = new HashSet<>();

    @Autowired
    public GoogleSheetsService(PlayerTagData repository, Sheets sheets) {
        this.repository = repository;
        this.sheets = sheets;
    }


    @Getter
    @Value("${sheets.id}")
    private String spreadsheetId;


    public void appendDataToSheet(String range, List<List<Object>> data) throws IOException {
        ValueRange body = new ValueRange().setValues(data);

        this.sheets.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();

    }
    private boolean cabeçalhoJaExiste(String range) throws IOException {
        ValueRange response = sheets.spreadsheets().values()
                .get(spreadsheetId, range)
                .execute();

        List<List<Object>> values = response.getValues();
        if (values != null && !values.isEmpty()) {
            List<Object> primeiraLinha = values.get(0);
            return primeiraLinha.contains("Team Name"); // ou qualquer outro nome fixo da primeira célula
        }
        return false;
    }
    private List<Object> criarCabecalho() {
        return Arrays.asList(
                "Team Name", "Battle Time", "Mode", "Result", "Duration",
                "Player #1", "Player #2", "Player #3",
                "Brawler P#1", "Brawler P#2", "Brawler P#3",
                "Opponent #1", "Opponent #2", "Opponent #3",
                "Opponent Brawler #1", "Opponent Brawler #2", "Opponent Brawler #3"
        );
    }

    public List<List<Object>> getInfo(List<TeamBattleDTO> battleDto) throws IOException {

        List<List<Object>> data = new ArrayList<>();

        if (!cabeçalhoJaExiste("A1:Q1")) {
            data.add(criarCabecalho());
        }



        for (TeamBattleDTO team : battleDto) {
            for (BrawlRequestMODEL.BattleLogInfo battle : team.getBattles()) {

                String id = battle.getBattle().getId();
                String battleTime = battle.getBattleTime();
                String uniqueKey = id + battleTime;

                if (cache.contains(uniqueKey)) continue;

                List<List<BrawlRequestMODEL.Player>> teams = battle.getBattle().getTeams();
                List<BrawlRequestMODEL.Player> aliados = teams.size() >= 1 ? teams.get(0) : new ArrayList<>();
                List<BrawlRequestMODEL.Player> oponentes = teams.size() >= 2 ? teams.get(1) : new ArrayList<>();

                List<BrawlRequestMODEL.Player> playersAliados = aliados.size() >= 3 ? aliados.subList(0, 3) : aliados;
                List<BrawlRequestMODEL.Player> playersOponentes = oponentes.size() >= 3 ? oponentes.subList(0, 3) : oponentes;

                List<String> nomesAliados = playersAliados.stream()
                        .map(p -> Optional.ofNullable(p.getName()).orElse("N/A"))
                        .collect(Collectors.toList());

                List<String> brawlersAliados = playersAliados.stream()
                        .map(p -> p.getBrawler() != null ? p.getBrawler().getName() : "N/A")
                        .collect(Collectors.toList());

                List<String> nomesOponentes = playersOponentes.stream()
                        .map(p -> Optional.ofNullable(p.getName()).orElse("N/A"))
                        .collect(Collectors.toList());

                List<String> brawlersOponentes = playersOponentes.stream()
                        .map(p -> p.getBrawler() != null ? p.getBrawler().getName() : "N/A")
                        .collect(Collectors.toList());

                List<Object> linha = new ArrayList<>();
                linha.add(team.getTeamName());
                linha.add(battleTime);
                linha.add(battle.getBattle().getMode());
                linha.add(battle.getBattle().getResult());
                linha.add(battle.getBattle().getDuration());

                preencher(linha, nomesAliados, 3);
                preencher(linha, brawlersAliados, 3);
                preencher(linha, nomesOponentes, 3);
                preencher(linha, brawlersOponentes, 3);

                data.add(linha);
                cache.add(uniqueKey);
            }
        }

        return data;
    }

    private void preencher(List<Object> destino, List<String> origem, int tamanhoEsperado) {
        for (int i = 0; i < tamanhoEsperado; i++) {
            if (i < origem.size()) {
                destino.add(origem.get(i));
            } else {
                destino.add("N/A");
            }
        }
    }

}


