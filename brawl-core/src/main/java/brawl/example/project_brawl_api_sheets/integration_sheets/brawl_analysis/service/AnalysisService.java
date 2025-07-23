package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.GeneralTeamInfo;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BattleMatch;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.BattleMatchRepository;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.TeamRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AnalysisService {
    private final TeamRepository teamRepository;

    public AnalysisService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public GeneralTeamInfo generalTeamInfo(String teamName) {
        Optional<TeamMODEL> teamOpt = teamRepository.findByNameTeamWithBattles(teamName);

        if (teamOpt.isPresent()) {
            TeamMODEL team = teamOpt.get();
            List<BattleMatch> battles = team.getBattles();

            GeneralTeamInfo info = new GeneralTeamInfo();
            info.setTeamName(team.getNameTeam());
            info.setNumberOfMatches(battles.size());

            long victories = battles.stream()
                    .filter(battle -> "victory".equalsIgnoreCase(battle.getResult()))
                    .count();

            info.setVictors(victories);
            info.setNumberOfMatches(battles.size());
            info.setWinrate(battles.isEmpty() ? 0.0 :
                    (double) victories / battles.size() * 100);

            return info;

        }

        return null;
    }
}


