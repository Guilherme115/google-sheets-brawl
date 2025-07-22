package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerMODEL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerMODEL,String> {



}
