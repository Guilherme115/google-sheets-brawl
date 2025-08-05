package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BrawlerMODEL
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerMODEL
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.BrawlerRepository
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.PlayerRepository
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ActiveProfiles
import org.springframework.transaction.annotation.Transactional
import spock.lang.Specification

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class BrawlDataServiceEntityCreationSpec extends Specification {

    // --- Injeção de Dependências Reais ---
    @Autowired
    BrawlDataService brawlDataService
    @Autowired
    PlayerRepository playerRepository
    @Autowired
    BrawlerRepository brawlerRepository

    // --- Mocks para Serviços Externos ---
    // Mockamos beans que não são o foco deste teste
    @MockBean
    GoogleSheetsService googleSheetsService
    @MockBean
    SyncService syncService
    @MockBean
    SheetExportService sheetExportService
    @MockBean
    TeamUpdateService teamUpdateService

    def "deve criar novos jogadores e brawlers que não existem no banco"() {
        given: "Um log de batalhas com 4 jogadores e 4 brawlers únicos"
        def battleLogDTO = createMockBattleLog()

        and: "O banco de dados está limpo"
        assert playerRepository.count() == 0
        assert brawlerRepository.count() == 0

        when: "O serviço processa o log"
        brawlDataService.processAndSaveBattleLog(battleLogDTO, [:])

        then: "Todos os 4 jogadores e 4 brawlers são criados"
        playerRepository.count() == 4
        brawlerRepository.count() == 4

        and: "Os dados de um jogador específico estão corretos"
        def player1 = playerRepository.findById("P1").orElse(null)
        assert player1 != null
        assert player1.name == "PlayerOne"
    }

    def "deve reutilizar jogadores e brawlers existentes sem criar duplicatas"() {
        given: "Um jogador e um brawler já existem no banco"
        playerRepository.save(new PlayerMODEL(tag: "P1", name: "PlayerOne_EXISTENTE"))
        brawlerRepository.save(new BrawlerMODEL(name: "SHELLY"))

        and: "Um log de batalhas que contém esse mesmo jogador e brawler"
        def battleLogDTO = createMockBattleLog()

        long initialPlayerCount = playerRepository.count()
        long initialBrawlerCount = brawlerRepository.count()

        when: "O serviço processa o log"
        brawlDataService.processAndSaveBattleLog(battleLogDTO, [:])

        then: "A contagem de jogadores e brawlers reflete a reutilização"
        // Mock tem P1, P2, P4, P5. P1 já existe. Devem ser criados 3 novos. Total = 4
        assert playerRepository.count() == initialPlayerCount + 3
        // Mock tem SHELLY, COLT, NITA, BULL. SHELLY já existe. Devem ser criados 3 novos. Total = 4
        assert brawlerRepository.count() == initialBrawlerCount + 3

        and: "O nome do jogador pre-existente NÃO foi alterado"
        def player1 = playerRepository.findById("P1").get()
        assert player1.name == "PlayerOne_EXISTENTE"
    }

    // --- Método Auxiliar para criar dados de teste ---
    private BattleLogReceiveDTO createMockBattleLog() {
        def mapper = new ObjectMapper()
        def json = """
        {
          "items": [
            {
              "battleTime": "2025-08-03T20:00:00.000Z",
              "battle": {
                "teams": [
                  [
                    {"tag": "#P1", "name": "PlayerOne", "brawler": {"name": "SHELLY"}},
                    {"tag": "#P2", "name": "PlayerTwo", "brawler": {"name": "COLT"}}
                  ],
                  [
                    {"tag": "#P4", "name": "PlayerFour", "brawler": {"name": "NITA"}},
                    {"tag": "#P5", "name": "PlayerFive", "brawler": {"name": "BULL"}}
                  ]
                ]
              }
            }
          ]
        }
        """
        return mapper.readValue(json, BattleLogReceiveDTO.class)
    }
}