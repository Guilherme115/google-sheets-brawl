package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.team;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player.BrawlerPerformanceDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player.ModePerformanceDto;

import java.util.List;

public record TeamCardDto(
        TeamRankingDto teamStats,
        List<TeamComparisonDto> top3Comparison,
        List<BrawlerPerformanceDto> bestBrawlers,
        List<ModePerformanceDto> bestModes
) {}