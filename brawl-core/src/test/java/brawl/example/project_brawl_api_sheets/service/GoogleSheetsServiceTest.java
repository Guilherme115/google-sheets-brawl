package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.TeamBattleDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BattleLog;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service.GoogleSheetsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.sheets.v4.Sheets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleSheetsServiceTest {

    @Mock
    Sheets sheets;

    @Mock
    Sheets.Spreadsheets spreadsheets;

    @InjectMocks
    GoogleSheetsService service;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setUp() throws Exception {
        when(sheets.spreadsheets()).thenReturn(spreadsheets);
    }

    private BattleLog loadBattleLog(String resourcePath) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(resourcePath);
        if (is == null) {
            throw new IllegalStateException("Arquivo não encontrado: " + resourcePath);
        }
        return mapper.readValue(is, BattleLog.class);
    }

    @Test
    @DisplayName("Deve gerar 17 Colunas, para passar no teste")
    void getInfo_17_COLLUMNS() throws Exception {
        BattleLog logInfo = loadBattleLog("Util/TestBrawlModel.json");
        List<BattleLog.BattleLogInfo> items = logInfo.getItems();
        List<TeamBattleDTO> batalhasDto = new ArrayList<>(List.of(new TeamBattleDTO("SolidName", items)));
        List<List<Object>> info = service.getInfo(batalhasDto);

        for (List<Object> linha : info) {
            assertEquals(17, linha.size(), "Cada linha deve ter exatamente 17 colunas");
        }
    }

    @Test
    @DisplayName("Deve gerar 17 colunas mesmo com campos vazios")
    void getInfo_17_COLLUMS_DATANULL() throws Exception {
        BattleLog logInfo = loadBattleLog("Util/TestBrawlModelFalse.json");
        List<BattleLog.BattleLogInfo> items = logInfo.getItems();
        List<TeamBattleDTO> batalhasDto = new ArrayList<>(List.of(new TeamBattleDTO("SolidName", items)));
        List<List<Object>> info = service.getInfo(batalhasDto);

        for (List<Object> linha : info) {
            assertEquals(17, linha.size(), "Cada linha deve ter exatamente 17 colunas");
        }
    }
}
