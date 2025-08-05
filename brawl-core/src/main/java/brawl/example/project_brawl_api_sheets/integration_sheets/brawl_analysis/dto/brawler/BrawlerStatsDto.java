package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler.BrawlerStatsProjection;

// Usamos 'record' para uma classe de dados concisa e imutável
public record BrawlerStatsDto(
        String brawlerName,
        long matchesPlayed,
        long victories,
        long defeats,
        long draws,
        double winRate,
        double pickRate
) {
    public static BrawlerStatsDto fromProjection(BrawlerStatsProjection projection, long totalPicksInFilter) {
        double winRate = (projection.getVictories() + projection.getDefeats()) > 0 ?
                ((double) projection.getVictories() / (projection.getVictories() + projection.getDefeats())) * 100.0 : 0.0;

        double pickRate = totalPicksInFilter > 0 ?
                ((double) projection.getMatchesPlayed() / totalPicksInFilter) * 100.0 : 0.0;

        return new BrawlerStatsDto(
                projection.getBrawlerName(),
                projection.getMatchesPlayed(),
                projection.getVictories(),
                projection.getDefeats(),
                projection.getDraws(),
                winRate,
                pickRate
        );
    }
}