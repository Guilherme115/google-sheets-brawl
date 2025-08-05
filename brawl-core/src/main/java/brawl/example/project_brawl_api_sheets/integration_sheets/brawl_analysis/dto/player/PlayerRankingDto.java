package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.player.PlayerRankingProjection;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerMODEL;

public record PlayerRankingDto(
        int rank,
        String playerName,
        String playerTag,
        String registeredTeam,
        long matchesPlayed,
        double winRate,
        long victories
) {
    public static PlayerRankingDto fromProjection(PlayerRankingProjection projection, PlayerMODEL player, int rank) {
        String teamName = player.getRegisteredTeams().isEmpty() ?
                "Sem Time" :
                player.getRegisteredTeams().iterator().next().getName();

        double winRate = projection.getMatchesPlayed() > 0 ?
                ((double) projection.getVictories() / projection.getMatchesPlayed()) * 100.0 : 0.0;

        return new PlayerRankingDto(
                rank,
                projection.getPlayerName(),
                "#" + projection.getPlayerTag(),
                teamName,
                projection.getMatchesPlayed(),
                winRate,
                projection.getVictories()
        );
    }
}