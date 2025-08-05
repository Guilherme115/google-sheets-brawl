package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player;


public record MapPerformanceDto(
        String map,
        long matchesPlayed,
        double winRate
) {}