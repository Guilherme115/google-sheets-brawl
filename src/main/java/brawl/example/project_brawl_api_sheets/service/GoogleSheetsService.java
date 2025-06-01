package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.dto.TeamBattleDTO;
import brawl.example.project_brawl_api_sheets.entity.PlayerTagData;
import brawl.example.project_brawl_api_sheets.model.BrawlRequestMODEL;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GoogleSheetsService {

    private final Sheets sheets;
    private final PlayerTagData repository;

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

        sheets.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();

    }

    public List<List<Object>> getInfo(List<TeamBattleDTO> battleDto) throws IOException {
        List<List<Object>> data = new ArrayList<>();

        // Cabeçalho da planilha
        data.add(Arrays.asList("Team Name", "Battle Time", "Mode", "Result", "Duration",
                "Player #1", "Player #2", "Player #3",
                "Brawler P#1", "Brawler P#2", "Brawler P#3",
                "Opponent #1", "Opponent #2", "Opponent #3",
                "Opponent Brawler #1", "Opponent Brawler #2", "Opponent Brawler #3"));

        for (TeamBattleDTO team : battleDto) {
            for (BrawlRequestMODEL.BattleLogInfo battle : team.getBattles()) {
                List<List<BrawlRequestMODEL.Player>> teams = battle.getBattle().getTeams();

                List<BrawlRequestMODEL.Player> aliados = new ArrayList<>();
                List<BrawlRequestMODEL.Player> oponentes = new ArrayList<>();

                if (teams.size() >= 2) {
                    aliados = teams.get(0);
                    oponentes = teams.get(1);
                } else if (teams.size() == 1) {
                    aliados = teams.get(0);
                }

                List<BrawlRequestMODEL.Player> playersAliados = aliados.size() > 3 ? aliados.subList(0, 3) : aliados;
                List<BrawlRequestMODEL.Player> playersOponentes = oponentes.size() > 3 ? oponentes.subList(0, 3) : oponentes;

                List<String> nomesAliados = playersAliados.stream()
                        .map(BrawlRequestMODEL.Player::getName)
                        .collect(Collectors.toList());

                List<String> brawlersAliados = playersAliados.stream()
                        .map(p -> p.getBrawler() != null ? p.getBrawler().getName() : "Not Found")
                        .collect(Collectors.toList());

                List<String> nomesOponentes = playersOponentes.stream()
                        .map(BrawlRequestMODEL.Player::getName)
                        .collect(Collectors.toList());

                List<String> brawlersOponentes = playersOponentes.stream()
                        .map(p -> p.getBrawler() != null ? p.getBrawler().getName() : "Not Found")
                        .collect(Collectors.toList());

                // Dados gerais
                String result = battle.getBattle().getResult();
                String battleTime = battle.getBattleTime();
                String mode = battle.getBattle().getMode();
                int duration = battle.getBattle().getDuration();

                List<Object> linha = new ArrayList<>();
                linha.add(team.getTeamName());
                linha.add(battleTime);
                linha.add(mode);
                linha.add(result);
                linha.add(duration);

                // Garantir que tenha sempre 3 campos (mesmo que com "N/A")
                preencher(linha, nomesAliados, 3);
                preencher(linha, brawlersAliados, 3);
                preencher(linha, nomesOponentes, 3);
                preencher(linha, brawlersOponentes, 3);

                data.add(linha);
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


//Aqui preciso receber um Team Model e as informações vou aramzenar em planilha.
//No Sync Service vou fazer o for que vai pegar do mongoDb e vai colcoar na planilha.
// Vamos fazer uma brinaceira com Threads pra atualizar esse no metodo constantemente


//Tags das equipes
//Faço um for pra pecorer as tags
//Cadastro as