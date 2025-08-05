package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.MatchTeamMODEL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface MatchTeamRepository extends JpaRepository<MatchTeamMODEL,Long> {


    /**
     * CORRIGIDO: Encontra todas as escalações (participações em partidas) de um time.
     * @param teamName O nome do time.
     * @return Uma lista de MatchTeamMODEL.
     */
    @Query("SELECT t FROM MatchTeamMODEL t LEFT JOIN FETCH t.battle WHERE t.nameTeam = :teamName")
    List<MatchTeamMODEL> findAllByNameTeamWithBattles(@Param("teamName") String teamName);

}
