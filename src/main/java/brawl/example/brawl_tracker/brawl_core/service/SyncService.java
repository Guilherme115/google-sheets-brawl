package brawl.example.brawl_tracker.brawl_core.service;

import brawl.example.brawl_tracker.brawl_core.dto.TeamBattleDTO;
import brawl.example.brawl_tracker.brawl_core.model.TeamMODEL;
import com.google.api.services.sheets.v4.Sheets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Service
public class SyncService {

    private final GoogleSheetsService googleSheetsService;
    private final BrawlService brawlService;
    private final TeamService service;
    private final Sheets sheetsService;
    ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public SyncService(Sheets sheetsService, TeamService service, BrawlService brawlService, GoogleSheetsService googleSheetsService) {
        this.sheetsService = sheetsService;
        this.service = service;
        this.brawlService = brawlService;
        this.googleSheetsService = googleSheetsService;
    }

    @Scheduled(fixedRate = 30000) // 30 segundos
    public void sync() {
        List<TeamBattleDTO> teamBattleDTOS = new ArrayList<>();
        List<TeamMODEL> playersTagsANDnameTEAM = service.getPlayersTagsANDnameTEAM();

        for (TeamMODEL teamMODEL : playersTagsANDnameTEAM) {
            if (teamMODEL.getPlayersTags().isEmpty()) continue;

            String mainTag = teamMODEL.getPlayersTags().get(0);
            TeamBattleDTO teams = brawlService.getTeams(mainTag, teamMODEL);
            teamBattleDTOS.add(teams);
        }

        try {
            List<List<Object>> info = googleSheetsService.getInfo(teamBattleDTOS);
            googleSheetsService.appendDataToSheet("A1", info);
            System.out.println("Sync Successful");
        } catch (IOException e) {
            System.err.println("Erro ao sincronizar: " + e.getMessage());
            e.printStackTrace();
        }
    }
}