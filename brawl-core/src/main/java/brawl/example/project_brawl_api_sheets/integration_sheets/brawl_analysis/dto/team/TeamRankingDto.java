package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.team;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.team.TeamStatsProjection;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamRegisterMODEL;

public record TeamRankingDto(
        int rank,
        String teamName,
        String logoUrl,
        String region,
        long matchesPlayed,
        double winRate,
        String winLossRecord // Ex: "15W - 5L"
) {
    public static TeamRankingDto fromProjection(TeamStatsProjection projection, TeamRegisterMODEL teamInfo, int rank) {
        long defeats = projection.getMatchesPlayed() - projection.getVictories();
        String winLossRecord = String.format("%dW - %dL", projection.getVictories(), defeats);

        double winRate = projection.getMatchesPlayed() > 0 ?
                ((double) projection.getVictories() / projection.getMatchesPlayed()) * 100.0 : 0.0;

        return new TeamRankingDto(
                rank,
                projection.getTeamName(),
                teamInfo != null ? teamInfo.getLogoUrl() : "N/A",
                teamInfo != null ? teamInfo.getRegion().getDisplayName() : "N/A",
                projection.getMatchesPlayed(),
                winRate,
                winLossRecord
        );
    }
}