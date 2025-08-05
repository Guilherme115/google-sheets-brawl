package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.player.*;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.player.PlayerRankingProjection;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.PlayerPerformanceRepository;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.PlayerRepository;
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
public class PlayerAnalysisService {

    private final PlayerRepository playerRepository;
    private final PlayerPerformanceRepository performanceRepository;
    private static final int TOP_LIST_LIMIT = 3; // Limite para brawlers/modos/mapas

    public Page<PlayerRankingDto> getPageOfPlayerRankings(Pageable pageable, String timePeriod, String map, String mode, String type) {
        LocalDateTime startTime = calculateStartTime(timePeriod);
        Page<PlayerRankingProjection> pageOfProjections = performanceRepository.findPlayerRankings(startTime, map, mode, type, pageable);

        List<String> playerTagsOnPage = pageOfProjections.getContent().stream()
                .map(PlayerRankingProjection::getPlayerTag)
                .toList();

        Map<String, PlayerMODEL> playerMap = playerRepository.findAllById(playerTagsOnPage).stream()
                .collect(Collectors.toMap(PlayerMODEL::getTag, p -> p));

        int startRank = pageable.getPageNumber() * pageable.getPageSize() + 1;

        List<PlayerRankingDto> dtoList = IntStream.range(0, pageOfProjections.getContent().size())
                .mapToObj(i -> {
                    PlayerRankingProjection proj = pageOfProjections.getContent().get(i);
                    PlayerMODEL player = playerMap.get(proj.getPlayerTag());
                    return PlayerRankingDto.fromProjection(proj, player, startRank + i);
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtoList, pageable, pageOfProjections.getTotalElements());
    }

    public Optional<PlayerCardDto> getPlayerCard(String playerTag, String timePeriod, String map, String mode, String type) {
        Optional<PlayerMODEL> playerOpt = playerRepository.findById(playerTag.replace("#", ""));
        if (playerOpt.isEmpty()) {
            return Optional.empty();
        }

        PlayerMODEL player = playerOpt.get();
        String teamName = player.getRegisteredTeams().stream().findFirst().map(team -> team.getName()).orElse("Sem Time");
        LocalDateTime startTime = calculateStartTime(timePeriod);

        // 1. Estatísticas Gerais
        OverallPlayerStatsDto overallStats = performanceRepository.findOverallStatsByPlayerTag(player.getTag(), startTime, map, mode, type)
                .map(proj -> new OverallPlayerStatsDto(
                        proj.getMatchesPlayed(),
                        proj.getMatchesPlayed() > 0 ? ((double) proj.getVictories() / proj.getMatchesPlayed()) * 100.0 : 0.0
                ))
                .orElse(new OverallPlayerStatsDto(0, 0.0));

        // 2. Brawlers de Destaque
        List<BrawlerPerformanceDto> signatureBrawlers = performanceRepository.findBrawlerPerformanceByPlayerTag(player.getTag(), PageRequest.of(0, TOP_LIST_LIMIT))
                .stream()
                .map(proj -> new BrawlerPerformanceDto(
                        proj.getBrawlerName(),
                        proj.getMatchesPlayed(),
                        proj.getMatchesPlayed() > 0 ? ((double) proj.getVictories() / proj.getMatchesPlayed()) * 100.0 : 0.0
                ))
                .collect(Collectors.toList());

        // 3. Melhores Modos
        List<ModePerformanceDto> bestModes = performanceRepository.findModePerformanceByPlayerTag(player.getTag(), PageRequest.of(0, TOP_LIST_LIMIT))
                .stream()
                .map(proj -> new ModePerformanceDto(
                        proj.getMode(),
                        proj.getMatchesPlayed(),
                        proj.getMatchesPlayed() > 0 ? ((double) proj.getVictories() / proj.getMatchesPlayed()) * 100.0 : 0.0
                ))
                .collect(Collectors.toList());

        // 4. Melhores Mapas
        List<MapPerformanceDto> bestMaps = performanceRepository.findMapPerformanceByPlayerTag(player.getTag(), PageRequest.of(0, TOP_LIST_LIMIT))
                .stream()
                .map(proj -> new MapPerformanceDto(
                        proj.getMap(),
                        proj.getMatchesPlayed(),
                        proj.getMatchesPlayed() > 0 ? ((double) proj.getVictories() / proj.getMatchesPlayed()) * 100.0 : 0.0
                ))
                .collect(Collectors.toList());

        PlayerCardDto card = new PlayerCardDto(player.getName(), "#" + player.getTag(), teamName, overallStats, signatureBrawlers, bestModes, bestMaps);
        return Optional.of(card);
    }

    private LocalDateTime calculateStartTime(String timePeriod) {
        if ("7d".equalsIgnoreCase(timePeriod)) return LocalDateTime.now().minus(7, ChronoUnit.DAYS);
        if ("30d".equalsIgnoreCase(timePeriod)) return LocalDateTime.now().minus(30, ChronoUnit.DAYS);
        return null;
    }
}