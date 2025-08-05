package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player;

public record OverallPlayerStatsDto(
        long matchesPlayed,
        double winRate
) {}