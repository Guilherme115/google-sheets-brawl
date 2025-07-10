package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.GeneralTeamInfo;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity.Battle;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity.TeamInfoBattle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnalysisService {

    private final RetrieveDataBase retrieveDataBase;


    public AnalysisService(RetrieveDataBase retrieveDataBase) {
        this.retrieveDataBase = retrieveDataBase;
    }


    public GeneralTeamInfo fetchGeneralData(String teamName) {
        List<TeamInfoBattle> allMatches = retrieveDataBase.getAllMatches();

        if (allMatches != null && !allMatches.isEmpty()) {

            Optional<TeamInfoBattle> teamData = allMatches.stream()
                    .filter(team -> teamName.equalsIgnoreCase(team.getTeamName()))
                    .findFirst();

            if (teamData.isPresent()) {
                TeamInfoBattle info = teamData.get();

                long victories = info.getBattleList().stream()
                        .filter(b -> "victory".equalsIgnoreCase(b.getResult()))
                        .count();
                int matches = info.getBattleList().size();

                double winrate = (double) victories / matches;


                GeneralTeamInfo generalInfo = new GeneralTeamInfo();
                generalInfo.setTeamName(info.getTeamName());
                generalInfo.setVictors(victories);
                generalInfo.setNumberOfMatches(info.getBattleList() != null ? matches : 0);

                return generalInfo;

            } else {
                System.out.println("Time não encontrado: " + teamName);
                return null;
            }
        } else {
            System.out.println("Nenhuma partida encontrada no banco.");
            return null;
        }
    }
}
//}
