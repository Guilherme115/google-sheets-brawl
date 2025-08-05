package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler;

public interface MapBrawlerStatsProjection {
    String getMap();
    String getMode();
    long getMatchesPlayed();
    long getVictories();
}