package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler.MapBrawlerStatsProjection;

public record MapBrawlerStatsDto(
        String map,
        String mode,
        long matchesPlayed,
        double winRate
) {
    public static MapBrawlerStatsDto fromProjection(MapBrawlerStatsProjection projection) {
        double winRate = projection.getMatchesPlayed() > 0 ?
                ((double) projection.getVictories() / projection.getMatchesPlayed()) * 100.0 : 0.0;

        return new MapBrawlerStatsDto(
                projection.getMap(),
                projection.getMode(),
                projection.getMatchesPlayed(),
                winRate
        );
    }
}