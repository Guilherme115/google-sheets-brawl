package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BrawlerMODEL;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BrawlerRepository extends JpaRepository<BrawlerMODEL,String> {
}
