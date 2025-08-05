package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler;

public interface PlayerBrawlerStatsProjection {
    String getPlayerName();
    String getPlayerTag();
    String getTeamName();
    long getMatchesPlayed();
    long getVictories();
}
