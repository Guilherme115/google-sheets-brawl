package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.dto.TeamBattleDTO;
import brawl.example.project_brawl_api_sheets.model.BrawlRequestMODEL;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class GoogleSheetsServiceTest {

    File file = new File("Util/TestBrawlModel.json");
    File failledFile = new File("Util/TestBrawlModelFalse.json");



    @InjectMocks
    GoogleSheetsService service;

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
    void getInfo_17_COLLUMS_DATANULL () throws IOException {
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