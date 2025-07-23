package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BattleMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
/*
Serviço que preciso ser refatorado por nao considerar teamName
 */
@Repository
public interface BattleMatchRepository extends JpaRepository <BattleMatch,Long> {
    /**
     * Verifica de forma otimizada se já existe uma batalha com o battleTime especificado.
     * @param battleTime O timestamp da batalha a ser verificado.
     * @return 'true' se uma batalha com este battleTime já existir, 'false' caso contrário.
     */
    boolean existsByBattleTime(String battleTime);

}


