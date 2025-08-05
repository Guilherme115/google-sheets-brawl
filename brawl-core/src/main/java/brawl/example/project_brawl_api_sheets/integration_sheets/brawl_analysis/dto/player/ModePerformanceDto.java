package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.player.ModePerformanceProjection;

public record ModePerformanceDto(
        String mode,
        long matchesPlayed,
        double winRate
) {
    // --- MÉTODO ADICIONADO AQUI ---
    public static ModePerformanceDto fromProjection(ModePerformanceProjection projection) {
        double winRate = projection.getMatchesPlayed() > 0 ?
                ((double) projection.getVictories() / projection.getMatchesPlayed()) * 100.0 : 0.0;
        return new ModePerformanceDto(projection.getMode(), projection.getMatchesPlayed(), winRate);
    }
}
