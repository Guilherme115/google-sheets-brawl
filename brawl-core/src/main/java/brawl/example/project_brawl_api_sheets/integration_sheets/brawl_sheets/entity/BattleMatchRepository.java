package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.entity;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BattleMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/*
Serviço que preciso ser refatorado por nao considerar teamName
 */
@Repository
public interface BattleMatchRepository extends JpaRepository <BattleMatch,Long> {

}
