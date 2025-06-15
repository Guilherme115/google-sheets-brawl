
<<<<<<<< HEAD:brawl-core/src/test/java/brawl/example/project_brawl_api_sheets/service/GoogleSheetsServiceTest.java
import brawl.example.project_brawl_api_sheets.integration_sheets.dto.TeamBattleDTO;
import brawl.example.project_brawl_api_sheets.integration_sheets.model.BrawlRequestMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.service.GoogleSheetsService;
========
package brawl.example.brawl_tracker.service;


import brawl.example.brawl_tracker.brawl_core.dto.TeamBattleDTO;
import brawl.example.brawl_tracker.brawl_core.model.BrawlRequestMODEL;
import brawl.example.brawl_tracker.brawl_core.service.GoogleSheetsService;
>>>>>>>> origin/main:src/test/java/brawl/example/brawl_tracker/service/GoogleSheetsServiceTest.java
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.services.sheets.v4.Sheets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleSheetsServiceTest {

    @Mock
    Sheets sheets; // <-- isso que está faltando
    @Mock
    Sheets.Spreadsheets spreadsheets;

    @InjectMocks
    GoogleSheetsService service;

    File file = new File("Util/TestBrawlModel.json");
    File failledFile = new File("Util/TestBrawlModelFalse.json");

    @BeforeEach
    void setUp() throws Exception {
        // Mockando o retorno sheets.spreadsheets()
        when(sheets.spreadsheets()).thenReturn(spreadsheets);
    }

    @Test
    @DisplayName("Deve gerar 17 Colunas, para passar no teste")
    void getInfo_17_COLLUMNS() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BrawlRequestMODEL logInfo = mapper.readValue(file, BrawlRequestMODEL.class);
        List<BrawlRequestMODEL.BattleLogInfo> items = logInfo.getItems();
        List<TeamBattleDTO> batalhasDto = new ArrayList<>(List.of(new TeamBattleDTO("SolidName", items)));
        List<List<Object>> info = service.getInfo(batalhasDto);

        for (List<Object> linha : info) {
            assertEquals(17, linha.size(), "Cada linha deve ter exatamente 17 colunas");
        }
    }

    @Test
    @DisplayName("Deve gerar 17 colunas mesmo com campos vazios")
    void getInfo_17_COLLUMS_DATANULL() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        BrawlRequestMODEL logInfo = mapper.readValue(failledFile, BrawlRequestMODEL.class);
        List<BrawlRequestMODEL.BattleLogInfo> items = logInfo.getItems();
        List<TeamBattleDTO> batalhasDto = new ArrayList<>(List.of(new TeamBattleDTO("SolidName", items)));
        List<List<Object>> info = service.getInfo(batalhasDto);

        for (List<Object> linha : info) {
            assertEquals(17, linha.size(), "Cada linha deve ter exatamente 17 colunas");
        }
    }
}

