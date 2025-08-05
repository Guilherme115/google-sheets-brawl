package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler.BrawlerStatsProjection;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler.MapBrawlerStatsProjection;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.brawler.PlayerBrawlerStatsProjection;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.player.*;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.team.TeamStatsProjection; // Corrigido de .tean para .team
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerPerformanceMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositório para acessar e agregar dados de performance dos jogadores (PlayerPerformanceMODEL).
 * Fornece métodos para consultar estatísticas complexas sobre Brawlers, Jogadores e Times
 * com suporte para filtros dinâmicos e paginação.
 */
@Repository
public interface PlayerPerformanceRepository extends JpaRepository<PlayerPerformanceMODEL, Long> {

    // =================================================================================================================
    // BRAWLER STATS
    // =================================================================================================================

    /**
     * Busca as estatísticas globais de Brawlers de forma paginada.
     * A ordenação é fornecida dinamicamente através do objeto Pageable.
     *
     * @return Uma página com as estatísticas agregadas por Brawler.
     */
    @Query(value = """
        SELECT
            p.brawler.name AS brawlerName,
            COUNT(p.id) AS matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) AS victories,
            SUM(CASE WHEN p.team.battle.result = 'defeat' THEN 1 ELSE 0 END) AS defeats,
            SUM(CASE WHEN p.team.battle.result = 'draw' THEN 1 ELSE 0 END) AS draws
        FROM PlayerPerformanceMODEL p
        WHERE
            p.team.teamType = :trackedType
            AND (:startTime IS NULL OR p.team.battle.battleTime >= :startTime)
            AND (:map IS NULL OR p.team.battle.map = :map)
            AND (:mode IS NULL OR p.team.battle.mode = :mode)
            AND (:type IS NULL OR p.team.battle.type = :type)
            AND (:teamName IS NULL OR p.team.nameTeam = :teamName)
        GROUP BY p.brawler.name
    """)
    Page<BrawlerStatsProjection> findBrawlerStats(
            @Param("startTime") LocalDateTime startTime,
            @Param("map") String map,
            @Param("mode") String mode,
            @Param("type") String type,
            @Param("teamName") String teamName,
            @Param("trackedType") TeamType trackedType,
            Pageable pageable
    );

    /**
     * Busca os melhores jogadores para um Brawler específico, de forma paginada.
     *
     * @param brawlerName O nome do Brawler para filtrar.
     * @return Uma página com as estatísticas dos jogadores para o Brawler especificado.
     */
    @Query("""
        SELECT
            p.player.name AS playerName,
            p.player.tag AS playerTag,
            p.team.nameTeam AS teamName,
            COUNT(p.id) AS matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) AS victories
        FROM PlayerPerformanceMODEL p
        WHERE
            p.brawler.name = :brawlerName
            AND p.team.teamType = :trackedType
            AND (:startTime IS NULL OR p.team.battle.battleTime >= :startTime)
            AND (:map IS NULL OR p.team.battle.map = :map)
            AND (:mode IS NULL OR p.team.battle.mode = :mode)
            AND (:type IS NULL OR p.team.battle.type = :type)
            AND (:teamName IS NULL OR p.team.nameTeam = :teamName)
        GROUP BY p.player.name, p.player.tag, p.team.nameTeam
    """)
    Page<PlayerBrawlerStatsProjection> findTopPlayersForBrawler(
            @Param("brawlerName") String brawlerName,
            @Param("startTime") LocalDateTime startTime,
            @Param("map") String map,
            @Param("mode") String mode,
            @Param("type") String type,
            @Param("teamName") String teamName,
            @Param("trackedType") TeamType trackedType,
            Pageable pageable
    );

    /**
     * Busca os melhores mapas para um Brawler específico, de forma paginada.
     *
     * @param brawlerName O nome do Brawler para filtrar.
     * @return Uma página com as estatísticas de mapas para o Brawler especificado.
     */
    @Query("""
        SELECT
            p.team.battle.map AS map,
            p.team.battle.mode AS mode,
            COUNT(p.id) AS matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) AS victories
        FROM PlayerPerformanceMODEL p
        WHERE
            p.brawler.name = :brawlerName
            AND p.team.teamType = :trackedType
            AND (:startTime IS NULL OR p.team.battle.battleTime >= :startTime)
            AND (:map IS NULL OR p.team.battle.map = :map)
            AND (:mode IS NULL OR p.team.battle.mode = :mode)
            AND (:type IS NULL OR p.team.battle.type = :type)
            AND (:teamName IS NULL OR p.team.nameTeam = :teamName)
        GROUP BY p.team.battle.map, p.team.battle.mode
    """)
    Page<MapBrawlerStatsProjection> findTopMapsForBrawler(
            @Param("brawlerName") String brawlerName,
            @Param("startTime") LocalDateTime startTime,
            @Param("map") String map,
            @Param("mode") String mode,
            @Param("type") String type,
            @Param("teamName") String teamName,
            @Param("trackedType") TeamType trackedType,
            Pageable pageable
    );

    // =================================================================================================================
    // PLAYER STATS
    // =================================================================================================================

    /**
     * Busca o ranking de jogadores com base em filtros, de forma paginada.
     * Focado em times do tipo 'TRACKED'.
     *
     * @return Uma página com o ranking de jogadores.
     */
    @Query("""
        SELECT
            p.player.name AS playerName,
            p.player.tag AS playerTag,
            COUNT(p.id) AS matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) AS victories
        FROM PlayerPerformanceMODEL p
        WHERE
            p.team.teamType = 'TRACKED'
            AND (:startTime IS NULL OR p.team.battle.battleTime >= :startTime)
            AND (:map IS NULL OR p.team.battle.map = :map)
            AND (:mode IS NULL OR p.team.battle.mode = :mode)
            AND (:type IS NULL OR p.team.battle.type = :type)
        GROUP BY p.player.tag, p.player.name
    """)
    Page<PlayerRankingProjection> findPlayerRankings(
            @Param("startTime") LocalDateTime startTime,
            @Param("map") String map,
            @Param("mode") String mode,
            @Param("type") String type,
            Pageable pageable
    );

    /**
     * Busca as estatísticas gerais de um jogador específico pela sua tag.
     *
     * @param playerTag A tag do jogador.
     * @return Um Optional contendo as estatísticas gerais do jogador.
     */
    @Query("""
        SELECT
            COUNT(p.id) as matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) as victories
        FROM PlayerPerformanceMODEL p
        WHERE
            p.player.tag = :playerTag AND p.team.teamType = 'TRACKED'
            AND (:startTime IS NULL OR p.team.battle.battleTime >= :startTime)
            AND (:map IS NULL OR p.team.battle.map = :map)
            AND (:mode IS NULL OR p.team.battle.mode = :mode)
            AND (:type IS NULL OR p.team.battle.type = :type)
    """)
    Optional<PlayerOverallStatsProjection> findOverallStatsByPlayerTag(
            @Param("playerTag") String playerTag,
            @Param("startTime") LocalDateTime startTime,
            @Param("map") String map,
            @Param("mode") String mode,
            @Param("type") String type
    );

    /**
     * Busca a performance de um jogador com cada Brawler.
     *
     * @param playerTag A tag do jogador.
     * @param pageable Parâmetro para limitar o número de resultados (e.g., top 5 brawlers).
     * @return Uma lista com a performance do jogador por Brawler.
     */
    @Query("""
        SELECT
            p.brawler.name as brawlerName,
            COUNT(p.id) as matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) as victories
        FROM PlayerPerformanceMODEL p
        WHERE p.player.tag = :playerTag AND p.team.teamType = 'TRACKED'
        GROUP BY p.brawler.name
        ORDER BY matchesPlayed DESC
    """)
    List<BrawlerPerformanceProjection> findBrawlerPerformanceByPlayerTag(@Param("playerTag") String playerTag, Pageable pageable);

    /**
     * Busca a performance de um jogador em cada modo de jogo.
     *
     * @param playerTag A tag do jogador.
     * @param pageable Parâmetro para limitar o número de resultados.
     * @return Uma lista com a performance do jogador por modo.
     */
    @Query("""
        SELECT
            p.team.battle.mode as mode,
            COUNT(p.id) as matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) as victories
        FROM PlayerPerformanceMODEL p
        WHERE p.player.tag = :playerTag AND p.team.teamType = 'TRACKED'
        GROUP BY p.team.battle.mode
        ORDER BY matchesPlayed DESC
    """)
    List<ModePerformanceProjection> findModePerformanceByPlayerTag(@Param("playerTag") String playerTag, Pageable pageable);

    /**
     * Busca a performance de um jogador em cada mapa.
     *
     * @param playerTag A tag do jogador.
     * @param pageable Parâmetro para limitar o número de resultados.
     * @return Uma lista com a performance do jogador por mapa.
     */
    @Query("""
        SELECT
            p.team.battle.map as map,
            COUNT(p.id) as matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) as victories
        FROM PlayerPerformanceMODEL p
        WHERE p.player.tag = :playerTag AND p.team.teamType = 'TRACKED'
        GROUP BY p.team.battle.map
        ORDER BY matchesPlayed DESC
    """)
    List<MapPerformanceProjection> findMapPerformanceByPlayerTag(@Param("playerTag") String playerTag, Pageable pageable);


    // =================================================================================================================
    // TEAM STATS
    // =================================================================================================================

    /**
     * Busca estatísticas de times de forma paginada usando uma query nativa.
     *
     * @return Uma página com as estatísticas agregadas por time.
     */
    @Query(
            value = """
            SELECT
                teamName,
                COUNT(*) AS matchesPlayed,
                SUM(is_victory) AS victories
            FROM (
                SELECT teamaname AS teamName, CASE WHEN winning_team_name = teamaname THEN 1 ELSE 0 END AS is_victory
                FROM match_set
                WHERE (:startTime IS NULL OR set_start_time >= :startTime)
                UNION ALL
                SELECT teambname AS teamName, CASE WHEN winning_team_name = teambname THEN 1 ELSE 0 END AS is_victory
                FROM match_set
                WHERE (:startTime IS NULL OR set_start_time >= :startTime)
            ) AS all_matches
            GROUP BY teamName
            """,
            countQuery = "SELECT COUNT(DISTINCT teamName) FROM (SELECT teamaname AS teamName FROM match_set UNION SELECT teambname AS teamName FROM match_set) AS distinct_teams",
            nativeQuery = true
    )
    Page<TeamStatsProjection> findTeamStats(
            @Param("startTime") LocalDateTime startTime,
            Pageable pageable
    );

    /**
     * Busca estatísticas de um time específico pelo nome.
     *
     * @param teamName O nome do time.
     * @return Um Optional contendo as estatísticas do time.
     */
    @Query(
            value = """
            SELECT
                teamName,
                COUNT(*) AS matchesPlayed,
                SUM(is_victory) AS victories
            FROM (
                SELECT teamaname AS teamName, CASE WHEN winning_team_name = teamaname THEN 1 ELSE 0 END AS is_victory FROM match_set
                UNION ALL
                SELECT teambname AS teamName, CASE WHEN winning_team_name = teambname THEN 1 ELSE 0 END AS is_victory FROM match_set
            ) AS all_matches
            WHERE teamName = :teamName
            GROUP BY teamName
            """,
            nativeQuery = true
    )
    Optional<TeamStatsProjection> findTeamStats(@Param("teamName") String teamName);

    /**
     * Busca a performance de um time com cada Brawler.
     *
     * @param teamName O nome do time.
     * @param pageable Parâmetro para limitar o número de resultados.
     * @return Uma lista da performance do time por Brawler.
     */
    @Query("""
        SELECT
            p.brawler.name as brawlerName,
            COUNT(p.id) as matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) as victories
        FROM PlayerPerformanceMODEL p
        WHERE p.team.nameTeam = :teamName AND p.team.teamType = 'TRACKED'
        GROUP BY p.brawler.name
        ORDER BY matchesPlayed DESC
    """)
    List<BrawlerPerformanceProjection> findBrawlerPerformanceByTeam(@Param("teamName") String teamName, Pageable pageable);

    /**
     * Busca a performance de um time em cada modo de jogo.
     *
     * @param teamName O nome do time.
     * @param pageable Parâmetro para limitar o número de resultados.
     * @return Uma lista da performance do time por modo.
     */
    @Query("""
        SELECT
            p.team.battle.mode as mode,
            COUNT(p.id) as matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) as victories
        FROM PlayerPerformanceMODEL p
        WHERE p.team.nameTeam = :teamName AND p.team.teamType = 'TRACKED'
        GROUP BY p.team.battle.mode
        ORDER BY matchesPlayed DESC
    """)
    List<ModePerformanceProjection> findModePerformanceByTeam(@Param("teamName") String teamName, Pageable pageable);

    // =================================================================================================================
    // UTILITY METHODS
    // =================================================================================================================

    /**
     * Conta o número total de "picks" (performances de jogadores) que correspondem aos filtros.
     * Útil para cálculos como Pick Rate, onde o total de picks é o denominador.
     *
     * @return A contagem total de performances de jogadores.
     */
    @Query("""
        SELECT COUNT(p.id)
        FROM PlayerPerformanceMODEL p
        WHERE
            p.team.teamType = :trackedType
            AND (:startTime IS NULL OR p.team.battle.battleTime >= :startTime)
            AND (:map IS NULL OR p.team.battle.map = :map)
            AND (:mode IS NULL OR p.team.battle.mode = :mode)
            AND (:type IS NULL OR p.team.battle.type = :type)
            AND (:teamName IS NULL OR p.team.nameTeam = :teamName)
    """)
    long countTotalPicks(
            @Param("startTime") LocalDateTime startTime,
            @Param("map") String map,
            @Param("mode") String mode,
            @Param("type") String type,
            @Param("teamName") String teamName,
            @Param("trackedType") TeamType trackedType
    );
}