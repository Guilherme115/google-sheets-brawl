
package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player;

import java.util.List;

public record PlayerCardDto(
        String playerName,
        String playerTag,
        String registeredTeam,
        OverallPlayerStatsDto overallStats,
        List<BrawlerPerformanceDto> signatureBrawlers,
        List<ModePerformanceDto> bestModes,
        List<MapPerformanceDto> bestMaps // Curiosidade adicionada
) {}