package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.entity.PlayerTagData;
import brawl.example.project_brawl_api_sheets.entity.PlayerTagEntity;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.*;

import java.io.IOException;

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
//[name,[jogador 1. jogador 2, jogador 3]

    private String getCell(List<Object> linha, int index) {
        return (linha.size() > index) ? linha.get(index).toString().trim() : "";
    }

    public HashMap<List<String>, String> getTags() throws IOException {
        String range = "BATTLES!B2:F"; // Colunas B até E

        ValueRange response = sheets.spreadsheets().values()
                .get(getSpreadsheetId(), range)
                .execute();

        HashMap<List<String>, String> tagOfTeam = new HashMap<>();
        List<List<Object>> linhas = response.getValues();
        List<List<Object>> statusColuna = new ArrayList<>();

        statusColuna.add(List.of("STATUS"));

        for (int i = 0; i < linhas.size(); i++) {
            List<Object> linha = linhas.get(i);
            List<String> tagsPlayer = new ArrayList<>();

            String tag1 = getCell(linha, 0);
            String tag2 = getCell(linha, 1);
            String tag3 = getCell(linha, 2);
            String tag4 = getCell(linha, 3);
            String teamName = getCell(linha, 4);

            // Validação dos obrigatórios
            if (tag1.isBlank() || tag2.isBlank() || teamName.isBlank()) {
                statusColuna.add(List.of(" Erro: TAG1, TAG2 e NOME são obrigatórios"));
                continue;
            }

            // Adiciona tags válidas
            tagsPlayer.add(tag1);
            tagsPlayer.add(tag2);
            if (!tag3.isBlank()) tagsPlayer.add(tag3);
            if (!tag4.isBlank()) tagsPlayer.add(tag4);


            tagOfTeam.put(tagsPlayer, teamName);


            statusColuna.add(List.of("✅ OK"));
        }


        ValueRange statusRange = new ValueRange()
                .setRange("G2")
                .setValues(statusColuna.subList(1, statusColuna.size())); //

        sheets.spreadsheets().values()
                .update(getSpreadsheetId(), "G2", statusRange)
                .setValueInputOption("RAW")
                .execute();

        return tagOfTeam;
    }


    public void salvarTagsDoGoogleSheets(Map<List<String>, String> mapaTags) {
        for (Map.Entry<List<String>, String> entry : mapaTags.entrySet()) {
            List<String> tags = entry.getKey();
            String teamName = entry.getValue();

            PlayerTagEntity entity = new PlayerTagEntity();
            entity.setName(teamName);
            entity.setTagsList(tags);

            if (!repository.existsByTags(entity.getTags())) {
                repository.save(entity);
            } else {
                System.out.println("🔁 Já existe no banco: " + entity.getTags());
            }
        }
    }

    public List<PlayerTagEntity> listarTodos() {
        return repository.findAll();
    }
}








