package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerPerformanceMODEL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerPerfomanceRepository extends JpaRepository<PlayerPerformanceMODEL, Long> {
}
