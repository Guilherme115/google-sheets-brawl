package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.controller;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.team.TeamCardDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.team.TeamRankingDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service.TeamAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis/teams")
@RequiredArgsConstructor
public class TeamAnalysisController {

    private final TeamAnalysisService teamAnalysisService;

    @GetMapping("/rankings")
    public ResponseEntity<Page<TeamRankingDto>> getTeamRankings(
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "all") String timePeriod
    ) {
        Page<TeamRankingDto> rankings = teamAnalysisService.getPageOfTeamRankings(pageable, timePeriod);
        return ResponseEntity.ok(rankings);
    }

    @GetMapping("/{teamName}")
    public ResponseEntity<TeamCardDto> getTeamCard(
            @PathVariable String teamName,
            @RequestParam(required = false, defaultValue = "all") String timePeriod
    ) {
        return teamAnalysisService.getTeamCard(teamName, timePeriod)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}