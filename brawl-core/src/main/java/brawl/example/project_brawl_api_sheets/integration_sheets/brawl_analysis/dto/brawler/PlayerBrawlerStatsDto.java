package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler.PlayerBrawlerStatsProjection;

public record PlayerBrawlerStatsDto(
        String playerName,
        String playerTag,
        String teamName,
        long matchesPlayed,
        double winRate
) {
    public static PlayerBrawlerStatsDto fromProjection(PlayerBrawlerStatsProjection projection) {
        double winRate = projection.getMatchesPlayed() > 0 ?
                ((double) projection.getVictories() / projection.getMatchesPlayed()) * 100.0 : 0.0;

        return new PlayerBrawlerStatsDto(
                projection.getPlayerName(),
                "#" + projection.getPlayerTag(),
                projection.getTeamName(),
                projection.getMatchesPlayed(),
                winRate
        );
    }
}