package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_analysis.projection.team.TeamStatsProjection;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.MatchSet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MatchSetRepository extends JpaRepository<MatchSet, Long> {

    List<MatchSet> findBySetStartTimeGreaterThanOrderBySetStartTimeAsc(LocalDateTime startTime);

    // --- QUERIES PARA ANÁLISE DE TIMES ---

    // --- CORREÇÃO AQUI ---
    // A query original foi envolvida por um "SELECT * FROM (...) AS final_results"
    // para resolver o "Unknown column" quando o Spring Data JPA adiciona a ordenação do Pageable.
    @Query(value = """
        SELECT * FROM (
            SELECT teamName, COUNT(*) AS matchesPlayed, SUM(is_victory) AS victories FROM (
                SELECT teamaname AS teamName, CASE WHEN winning_team_name = teamaname THEN 1 ELSE 0 END AS is_victory
                FROM match_set
                WHERE (:startTime IS NULL OR set_start_time >= :startTime)
                UNION ALL
                SELECT teambname AS teamName, CASE WHEN winning_team_name = teambname THEN 1 ELSE 0 END AS is_victory
                FROM match_set
                WHERE (:startTime IS NULL OR set_start_time >= :startTime)
            ) AS all_matches
            GROUP BY teamName
        ) AS final_results
    """,
            countQuery = """
        SELECT COUNT(DISTINCT teamName) FROM (
            SELECT teamaname AS teamName FROM match_set WHERE (:startTime IS NULL OR set_start_time >= :startTime)
            UNION
            SELECT teambname AS teamName FROM match_set WHERE (:startTime IS NULL OR set_start_time >= :startTime)
        ) AS distinct_teams
    """,
            nativeQuery = true)
    Page<TeamStatsProjection> findTeamStats(
            @Param("startTime") LocalDateTime startTime,
            Pageable pageable
    );

    @Query(value = """
        SELECT teamName, COUNT(*) AS matchesPlayed, SUM(is_victory) AS victories FROM (
            SELECT teamaname AS teamName, CASE WHEN winning_team_name = teamaname THEN 1 ELSE 0 END AS is_victory FROM match_set
            WHERE (:startTime IS NULL OR set_start_time >= :startTime)
            UNION ALL
            SELECT teambname AS teamName, CASE WHEN winning_team_name = teambname THEN 1 ELSE 0 END AS is_victory FROM match_set
            WHERE (:startTime IS NULL OR set_start_time >= :startTime)
        ) AS all_matches
        WHERE teamName = :teamName
        GROUP BY teamName
    """, nativeQuery = true)
    Optional<TeamStatsProjection> findTeamStats(
            @Param("startTime") LocalDateTime startTime,
            @Param("teamName") String teamName
    );
}