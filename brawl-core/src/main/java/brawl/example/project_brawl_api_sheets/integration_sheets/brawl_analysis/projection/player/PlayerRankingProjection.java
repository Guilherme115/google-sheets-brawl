package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.player;

public interface PlayerRankingProjection {
    String getPlayerName();
    String getPlayerTag();
    long getMatchesPlayed();
    long getVictories();
}
