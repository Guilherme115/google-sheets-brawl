package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service;

import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.model.ValueRange;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class GoogleSheetsService {

    private final Sheets sheets;

    @Getter
    @Value("${google.sheets.id}")
    private String spreadsheetId;

    public GoogleSheetsService(Sheets sheets) {
        this.sheets = sheets;
    }

    public void ensureHeaderExists() throws IOException {
        // O range agora vai de A até X para acomodar a nova coluna
        String range = "A1:X1";
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

    public void appendDataToSheet(List<List<Object>> data) throws IOException {
        if (data == null || data.isEmpty()) {
            log.warn("Nenhum dado para anexar à planilha.");
            return;
        }

        String range = "A1";
        ValueRange body = new ValueRange().setValues(data);

        this.sheets.spreadsheets().values()
                .append(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();

        log.info("{} linhas de dados anexadas à planilha com sucesso.", data.size());
    }

    private List<Object> createHeader() {
        // Cabeçalho final com a coluna "Opponent Name"
        return Arrays.asList(
                "Team Name", "Opponent Name", "Battle Time", "Mode", "Result", "Duration",
                "Player #1", "Tag #1", "Brawler #1",
                "Player #2", "Tag #2", "Brawler #2",
                "Player #3", "Tag #3", "Brawler #3",
                "Opponent #1", "Opponent Tag #1", "Opponent Brawler #1",
                "Opponent #2", "Opponent Tag #2", "Opponent Brawler #2",
                "Opponent #3", "Opponent Tag #3", "Opponent Brawler #3"
        );
    }
}