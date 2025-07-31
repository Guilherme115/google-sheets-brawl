package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BattleMatch;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.projects.ModeStatsProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface BattleMatchRepository extends JpaRepository <BattleMatch,Long> {
    /**
     * Busca um conjunto de battleTimes que já existem no banco de dados.
     * Otimizado para verificar múltiplas batalhas de uma só vez.
     * @param battleTimes Uma lista de timestamps de batalha a serem verificados.
     * @return Um Set<String> contendo apenas os battleTimes que já existem.
     */
    @Query("SELECT b.battleTime FROM BattleMatch b WHERE b.battleTime IN :battleTimes")
    Set<String> findExistingBattleTimes(@Param("battleTimes") List<String> battleTimes);

    // Consulta otimizada para a análise de performance por modo de jogo
    // Esta consulta já estava correta, mas se beneficia da clareza do modelo.
    @Query("""
        SELECT
            b.mode AS mode,
            COUNT(DISTINCT b.internalId) AS totalMatches,
            SUM(CASE WHEN b.result = 'victory' THEN 1 ELSE 0 END) AS totalVictories
        FROM
            BattleMatch b JOIN b.teams t
        WHERE
            t.nameTeam = :teamName AND t.teamType = 'MY_TEAM'
        GROUP BY
            b.mode
        ORDER BY totalMatches DESC
    """)
    List<ModeStatsProjection> getPerformanceByMode(@Param("teamName") String teamName);
    List<BattleMatch> findByBattleTimeGreaterThanOrderByBattleTimeAsc(String battleTime);

}