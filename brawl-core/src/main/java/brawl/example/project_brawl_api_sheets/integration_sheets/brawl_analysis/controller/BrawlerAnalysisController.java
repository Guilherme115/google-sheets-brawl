package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.controller;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler.BrawlerDetailDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler.BrawlerStatsDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service.BrawlerAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/analysis/brawlers")
@RequiredArgsConstructor
public class BrawlerAnalysisController {

    private final BrawlerAnalysisService brawlerAnalysisService;

    @GetMapping("/tierlist")
    public ResponseEntity<Page<BrawlerStatsDto>> getBrawlerTierList(
            Pageable pageable, // O Spring cria isso a partir dos parâmetros ?page=0&size=10&sort=...
            @RequestParam(required = false, defaultValue = "all") String timePeriod,
            @RequestParam(required = false) String map,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String teamName
    ) {
        Page<BrawlerStatsDto> tierList = brawlerAnalysisService.getBrawlerTierList(pageable, timePeriod, map, mode, type, teamName);
        return ResponseEntity.ok(tierList);
    }

    @GetMapping("/{brawlerName}")
    public ResponseEntity<BrawlerDetailDto> getBrawlerDetails(
            @PathVariable String brawlerName,
            @RequestParam(required = false, defaultValue = "all") String timePeriod,
            @RequestParam(required = false) String map,
            @RequestParam(required = false) String mode,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String teamName
    ) {
        BrawlerDetailDto details = brawlerAnalysisService.getBrawlerDetails(brawlerName, timePeriod, map, mode, type, teamName);
        return ResponseEntity.ok(details);
    }
}