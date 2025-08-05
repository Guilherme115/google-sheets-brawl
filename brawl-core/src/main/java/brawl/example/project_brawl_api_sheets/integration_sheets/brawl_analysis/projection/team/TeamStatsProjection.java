package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.team;

public interface TeamStatsProjection {
    String getTeamName();
    long getMatchesPlayed();
    long getVictories();
}