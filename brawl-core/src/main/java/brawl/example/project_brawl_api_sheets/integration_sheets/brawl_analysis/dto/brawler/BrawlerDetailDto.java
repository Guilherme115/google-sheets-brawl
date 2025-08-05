package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler;

import java.util.List;

public record BrawlerDetailDto(
        BrawlerStatsDto overallStats,
        List<PlayerBrawlerStatsDto> topPlayers,
        List<MapBrawlerStatsDto> bestMaps
) {}