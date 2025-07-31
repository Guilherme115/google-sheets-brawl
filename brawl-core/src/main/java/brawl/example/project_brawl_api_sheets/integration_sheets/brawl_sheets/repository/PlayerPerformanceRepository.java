package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerPerformanceMODEL;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.projects.BrawlerStatsProjection;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.projects.BrawlerTierProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerPerformanceRepository extends JpaRepository<PlayerPerformanceMODEL, Long> {

    // Consulta CORRIGIDA para performance de brawler por time
    @Query("""
        SELECT
            p.brawler.name AS brawlerName,
            COUNT(p.id) AS totalMatches,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) AS totalVictories
        FROM
            PlayerPerformanceMODEL p
        WHERE
            p.team.nameTeam = :teamName
        GROUP BY
            p.brawler.name
        ORDER BY
            totalMatches DESC
    """)
    List<BrawlerStatsProjection> getBrawlerPerformanceByTeam(@Param("teamName") String teamName);

    // Consulta CORRIGIDA para a análise de Meta Global (Tier List de Brawlers)
    @Query("""
        SELECT
            p.brawler.name AS brawlerName,
            COUNT(p.id) AS matchesPlayed,
            SUM(CASE WHEN p.team.battle.result = 'victory' THEN 1 ELSE 0 END) AS victories
        FROM
            PlayerPerformanceMODEL p
        WHERE
            p.team.teamType = 'MY_TEAM'
        GROUP BY
            p.brawler.name
        ORDER BY
            matchesPlayed DESC
    """)
    List<BrawlerTierProjection> getGlobalBrawlerStats();
}