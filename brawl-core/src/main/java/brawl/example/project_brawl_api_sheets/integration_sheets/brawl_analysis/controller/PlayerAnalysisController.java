package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.controller;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player.PlayerCardDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player.PlayerRankingDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service.PlayerAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis/players")
@RequiredArgsConstructor
public class PlayerAnalysisController {

    private final PlayerAnalysisService playerAnalysisService;

    @GetMapping("/rankings")
    public ResponseEntity<Page<PlayerRankingDto>> getPlayerRankings(
            Pageable pageable,
            @RequestParam(required = false, defaultValue = "all") String timePeriod,
            @RequestParam(required = false) String map,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String type
    ) {
        Page<PlayerRankingDto> rankings = playerAnalysisService.getPageOfPlayerRankings(pageable, timePeriod, map, mode, type);
        return ResponseEntity.ok(rankings);
    }

    @GetMapping("/{playerTag}")
    public ResponseEntity<PlayerCardDto> getPlayerCard(
            @PathVariable String playerTag,
            @RequestParam(required = false, defaultValue = "all") String timePeriod,
            @RequestParam(required = false) String map,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String type
    ) {
        return playerAnalysisService.getPlayerCard(playerTag.replace("#", ""), timePeriod, map, mode, type)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}