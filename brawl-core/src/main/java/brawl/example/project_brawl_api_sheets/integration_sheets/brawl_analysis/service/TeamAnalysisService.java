package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service;


import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player.BrawlerPerformanceDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player.ModePerformanceDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.team.TeamCardDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.team.TeamComparisonDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.team.TeamRankingDto;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.team.TeamStatsProjection;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamRegisterMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.MatchSetRepository;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.PlayerPerformanceRepository;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class TeamAnalysisService {

    private final MatchSetRepository matchSetRepository;
    private final PlayerPerformanceRepository performanceRepository;
    private final TeamRepository teamRegisterRepository;
    private static final int TOP_LIST_LIMIT = 3;

    public Page<TeamRankingDto> getPageOfTeamRankings(Pageable pageable, String timePeriod) {
        LocalDateTime startTime = calculateStartTime(timePeriod);
        Page<TeamStatsProjection> pageOfProjections = matchSetRepository.findTeamStats(startTime, pageable);

        List<String> teamNamesOnPage = pageOfProjections.getContent().stream().map(TeamStatsProjection::getTeamName).toList();
        Map<String, TeamRegisterMODEL> teamInfoMap = teamRegisterRepository.findAllByNameIn(teamNamesOnPage).stream()
                .collect(Collectors.toMap(TeamRegisterMODEL::getName, team -> team));

        int startRank = pageable.getPageNumber() * pageable.getPageSize() + 1;
        List<TeamRankingDto> dtoList = IntStream.range(0, pageOfProjections.getContent().size())
                .mapToObj(i -> {
                    TeamStatsProjection proj = pageOfProjections.getContent().get(i);
                    TeamRegisterMODEL teamInfo = teamInfoMap.get(proj.getTeamName());
                    return TeamRankingDto.fromProjection(proj, teamInfo, startRank + i);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, pageOfProjections.getTotalElements());
    }

    public Optional<TeamCardDto> getTeamCard(String teamName, String timePeriod) {
        LocalDateTime startTime = calculateStartTime(timePeriod);
        Optional<TeamStatsProjection> mainTeamProjectionOpt = matchSetRepository.findTeamStats(startTime, teamName);

        if (mainTeamProjectionOpt.isEmpty()) return Optional.empty();

        TeamRegisterMODEL teamInfo = teamRegisterRepository.findByName(teamName).orElse(null);
        TeamRankingDto mainTeamStats = TeamRankingDto.fromProjection(mainTeamProjectionOpt.get(), teamInfo, 0);

        Page<TeamStatsProjection> top3Projections = matchSetRepository.findTeamStats(startTime, PageRequest.of(0, 4));
        List<TeamComparisonDto> top3Comparison = top3Projections.getContent().stream()
                .filter(proj -> !proj.getTeamName().equalsIgnoreCase(teamName))
                .limit(3)
                .map(TeamComparisonDto::fromProjection)
                .collect(Collectors.toList());

        List<BrawlerPerformanceDto> bestBrawlers = performanceRepository.findBrawlerPerformanceByTeam(teamName, PageRequest.of(0, TOP_LIST_LIMIT))
                .stream().map(BrawlerPerformanceDto::fromProjection).collect(Collectors.toList());

        List<ModePerformanceDto> bestModes = performanceRepository.findModePerformanceByTeam(teamName, PageRequest.of(0, TOP_LIST_LIMIT))
                .stream().map(ModePerformanceDto::fromProjection).collect(Collectors.toList());

        TeamCardDto card = new TeamCardDto(mainTeamStats, top3Comparison, bestBrawlers, bestModes);
        return Optional.of(card);
    }

    private LocalDateTime calculateStartTime(String timePeriod) {
        if ("7d".equalsIgnoreCase(timePeriod)) return LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        if ("30d".equalsIgnoreCase(timePeriod)) return LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        return null;
    }
}