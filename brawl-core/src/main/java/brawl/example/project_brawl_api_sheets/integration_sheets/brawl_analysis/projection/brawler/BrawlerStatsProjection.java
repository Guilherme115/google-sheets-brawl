package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler;

public interface BrawlerStatsProjection {
    String getBrawlerName();
    long getMatchesPlayed();
    long getVictories();
    long getDefeats();
    long getDraws();
}