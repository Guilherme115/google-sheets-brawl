package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamMODEL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface TeamRepository extends JpaRepository<TeamMODEL,Long> {


    @Query("SELECT t FROM TeamMODEL t LEFT JOIN FETCH t.battles WHERE t.nameTeam = :teamName")
    Optional<TeamMODEL> findByNameTeamWithBattles(@Param("teamName") String teamName);
}


