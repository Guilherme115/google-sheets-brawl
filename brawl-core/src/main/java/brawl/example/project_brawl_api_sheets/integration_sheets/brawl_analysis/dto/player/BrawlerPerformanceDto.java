package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.player.BrawlerPerformanceProjection;

public record BrawlerPerformanceDto(
        String brawlerName,
        long matchesPlayed,
        double winRate
) {
    public static BrawlerPerformanceDto fromProjection(BrawlerPerformanceProjection projection) {
        double winRate = projection.getMatchesPlayed() > 0 ?
                ((double) projection.getVictories() / projection.getMatchesPlayed()) * 100.0 : 0.0;
        return new BrawlerPerformanceDto(projection.getBrawlerName(), projection.getMatchesPlayed(), winRate);
    }
}
