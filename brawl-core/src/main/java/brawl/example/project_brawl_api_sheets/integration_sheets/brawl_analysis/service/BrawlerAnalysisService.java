    package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.service;

    import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler.BrawlerDetailDto;
    import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler.BrawlerStatsDto;
    import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler.MapBrawlerStatsDto;
    import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.dto.brawler.PlayerBrawlerStatsDto;
    import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler.BrawlerStatsProjection;
    import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler.MapBrawlerStatsProjection;
    import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler.PlayerBrawlerStatsProjection;
    import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamType;
    import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.PlayerPerformanceRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.PageRequest;
    import org.springframework.data.domain.Pageable;
    import org.springframework.stereotype.Service;

    import java.time.LocalDateTime;
    import java.time.temporal.ChronoUnit;
    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class BrawlerAnalysisService {

        private final PlayerPerformanceRepository performanceRepository;
        private static final int TOP_LIST_LIMIT = 5;

        // --- MÉTODO ATUALIZADO ---
        public Page<BrawlerStatsDto> getBrawlerTierList(Pageable pageable, String timePeriod, String map, String mode, String type, String teamName) {
            LocalDateTime startTime = calculateStartTime(timePeriod);

            // 1. A chamada ao repositório agora passa o Pageable
            Page<BrawlerStatsProjection> statsPage = performanceRepository.findBrawlerStats(startTime, map, mode, type, teamName, TeamType.TRACKED, pageable);

            // 2. O total de picks continua sendo calculado com os mesmos filtros
            long totalPicksInFilter = performanceRepository.countTotalPicks(startTime, map, mode, type, teamName, TeamType.TRACKED);

            // 3. O Spring Data Page tem um método .map() que facilita a conversão
            return statsPage.map(stat -> BrawlerStatsDto.fromProjection(stat, totalPicksInFilter));
        }

        // --- MÉTODO ATUALIZADO ---
        public BrawlerDetailDto getBrawlerDetails(String brawlerName, String timePeriod, String map, String mode, String type, String teamName) {
            LocalDateTime startTime = calculateStartTime(timePeriod);

            // 1. Para obter as estatísticas gerais, buscamos a tier list completa (sem paginação no service)
            // e depois filtramos pelo brawler desejado.
            List<BrawlerStatsProjection> allStatsInFilter = performanceRepository.findBrawlerStats(startTime, map, mode, type, teamName, TeamType.TRACKED, Pageable.unpaged()).getContent();
            long totalPicksInFilter = performanceRepository.countTotalPicks(startTime, map, mode, type, teamName, TeamType.TRACKED);

            BrawlerStatsDto overallStats = allStatsInFilter.stream()
                    .filter(s -> s.getBrawlerName().equalsIgnoreCase(brawlerName))
                    .findFirst()
                    .map(proj -> BrawlerStatsDto.fromProjection(proj, totalPicksInFilter))
                    .orElse(null);

            // 2. Busca os top 5 players, agora recebendo um Page e pegando o conteúdo
            Page<PlayerBrawlerStatsProjection> topPlayersPage = performanceRepository.findTopPlayersForBrawler(
                    brawlerName, startTime, map, mode, type, teamName, TeamType.TRACKED, PageRequest.of(0, TOP_LIST_LIMIT));
            List<PlayerBrawlerStatsDto> topPlayersDto = topPlayersPage.getContent().stream()
                    .map(PlayerBrawlerStatsDto::fromProjection)
                    .collect(Collectors.toList());

            // 3. Busca os top 5 mapas, agora recebendo um Page e pegando o conteúdo
            Page<MapBrawlerStatsProjection> topMapsPage = performanceRepository.findTopMapsForBrawler(
                    brawlerName, startTime, map, mode, type, teamName, TeamType.TRACKED, PageRequest.of(0, TOP_LIST_LIMIT));
            List<MapBrawlerStatsDto> topMapsDto = topMapsPage.getContent().stream()
                    .map(MapBrawlerStatsDto::fromProjection)
                    .collect(Collectors.toList());

            return new BrawlerDetailDto(overallStats, topPlayersDto, topMapsDto);
        }

        private LocalDateTime calculateStartTime(String timePeriod) {
            if ("7d".equalsIgnoreCase(timePeriod)) {
                return LocalDateTime.now().minus(7, ChronoUnit.DAYS);
            }
            if ("30d".equalsIgnoreCase(timePeriod)) {
                return LocalDateTime.now().minus(30, ChronoUnit.DAYS);
            }
            return null;
        }
    }