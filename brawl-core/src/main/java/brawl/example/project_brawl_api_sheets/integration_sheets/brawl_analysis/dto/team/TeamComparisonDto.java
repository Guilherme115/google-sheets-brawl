package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.team;


import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.team.TeamStatsProjection;

public record TeamComparisonDto(
        int rank,
        String teamName,
        double winRate
) {
    // --- MÉTODO ADICIONADO AQUI ---
    public static TeamComparisonDto fromProjection(TeamStatsProjection projection) {
        double winRate = projection.getMatchesPlayed() > 0 ?
                ((double) projection.getVictories() / projection.getMatchesPlayed()) * 100.0 : 0.0;
        // O rank não vem da projeção, será definido no serviço
        return new TeamComparisonDto(0, projection.getTeamName(), winRate);
    }
}