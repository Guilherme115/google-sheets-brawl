package brawl.example.project_brawl_api_sheets.service;

import brawl.example.project_brawl_api_sheets.entity.PlayerTagData;
import brawl.example.project_brawl_api_sheets.entity.PlayerTagEntity;
import brawl.example.project_brawl_api_sheets.entity.SaveMongo;
import brawl.example.project_brawl_api_sheets.model.BrawlRequestMODEL;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hibernate.loader.internal.AliasConstantsHelper.get;

@Service
public class SyncService {

    private static final Logger log = LoggerFactory.getLogger(SyncService.class);
    private final BrawlService brawlService;
    private final GoogleSheetsService sheetsService;
    private final PlayerTagData playerTagData;
    private final SaveMongo saveMongo;

    public SyncService(BrawlService brawlService, GoogleSheetsService sheetsService, PlayerTagData playerTagData, SaveMongo saveMongo) {
        this.brawlService = brawlService;
        this.sheetsService = sheetsService;
        this.playerTagData = playerTagData;
        this.saveMongo = saveMongo;
    }


    public void filterTeamUnique() {
        try {
            HashMap<List<String>, String> tags = sheetsService.getTags();
            saveMongo.salvarTeamsNoMongo(tags);

            // Itera em TODAS as equipes
            for (Map.Entry<List<String>, String> entry : tags.entrySet()) {
                List<String> tagList = entry.getKey();
                String teamName = entry.getValue();

                if (tagList.isEmpty()) continue;
                if (playerTagData.existsByTags(tagList.get(0))) continue;

                String mainTag = tagList.get(0);
                Map<String, List<BrawlRequestMODEL.BattleLogInfo>> result = brawlService.getTeams(mainTag, tags);
                log.info("Equipe '{}' sincronizada com {} partidas.", teamName, result.size());


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
