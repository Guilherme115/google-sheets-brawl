package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.player;

public interface MapPerformanceProjection {
    String getMap();
    long getMatchesPlayed();
    long getVictories();
}