package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BattleMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime; // <-- IMPORT NECESSÁRIO
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Repository
public interface BattleMatchRepository extends JpaRepository<BattleMatch, Long> {

    /**
     * Busca batalhas cujo battleTime é maior que o valor fornecido, ordenado pelo tempo.
     * Usado pelo SheetExportService para encontrar novas partidas para exportar.
     * O parâmetro agora é do tipo LocalDateTime para garantir a comparação correta de datas.
     */
    @Query("SELECT b FROM BattleMatch b WHERE b.battleTime > :battleTime ORDER BY b.battleTime ASC")
    List<BattleMatch> findByBattleTimeGreaterThanOrderByBattleTimeAsc(@Param("battleTime") LocalDateTime battleTime); // <-- CORRIGIDO para LocalDateTime

    /**
     * Recebe uma coleção de 'battleTimes' (agora como LocalDateTime) e retorna um Set contendo
     * apenas aqueles que já existem na tabela 'battle_match'.
     * Usado pelo BrawlDataService para evitar salvar duplicatas de forma eficiente.
     */
    @Query("SELECT b.battleTime FROM BattleMatch b WHERE b.battleTime IN :battleTimes")
    Set<LocalDateTime> findExistingBattleTimes(@Param("battleTimes") Collection<LocalDateTime> battleTimes); // <-- CORRIGIDO para Collection e Set de LocalDateTime

}