package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.dto.TeamBattleDTO;
import brawl.example.project_brawl_api_sheets.model.TeamMODEL;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
@Service
public class SyncService {

    private final GoogleSheetsService googleSheetsService;
    private final BrawlService brawlService;
    private final TeamService service;
    private final Sheets sheetsService;

     @Autowired
    public SyncService(Sheets sheetsService, TeamService service, BrawlService brawlService, GoogleSheetsService googleSheetsService) {
        this.sheetsService = sheetsService;
        this.service = service;
        this.brawlService = brawlService;
        this.googleSheetsService = googleSheetsService;
    }

    public String Sync() {
        List<TeamBattleDTO> teamBattleDTOS = new ArrayList<>();
        for (TeamMODEL teamMODEL : service.getPlayerInfo()) {
            String teamName = teamMODEL.getTeamName();
            String mainTag = teamMODEL.getPlayersTags().get(0);
            TeamBattleDTO teams = brawlService.getTeams(mainTag, teamMODEL);
            teamBattleDTOS.add(teams);

        }
        try {
            List<List<Object>> info = googleSheetsService.getInfo(teamBattleDTOS);
            googleSheetsService.appendDataToSheet("A1", info);

            return "Sync Successful";


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
