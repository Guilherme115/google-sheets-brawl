package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.BrawlerMODEL
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerMODEL
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamRegisterMODEL
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.BrawlerRepository
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.MatchSetRepository
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
class BrawlDataServiceSetIntegrationSpec extends Specification {

    // --- Injeção de Dependências Reais ---
    @Autowired
    BrawlDataService brawlDataService
    @Autowired
    MatchSetRepository matchSetRepository
    @Autowired
    PlayerRepository playerRepository
    @Autowired
    BrawlerRepository brawlerRepository

    // --- Mocks para Serviços Externos ---
    @MockBean
    GoogleSheetsService googleSheetsService
    @MockBean
    SyncService syncService
    @MockBean
    SheetExportService sheetExportService
    @MockBean
    TeamUpdateService teamUpdateService

    def "deve agrupar um set MD3 em um único MatchSet com o resultado correto"() {
        given: "Um log de batalhas contendo apenas um set MD3 que terminou 2-1"
        def trackedTeam = new TeamRegisterMODEL(name: "Time Vencedor")
        def playerTagToTeamMap = ["P1": trackedTeam]
        def battleLogDTO = createMockMD3Log() // Usa um mock específico para este teste

        when: "O serviço processa o log de batalhas"
        brawlDataService.processAndSaveBattleLog(battleLogDTO, playerTagToTeamMap)

        then: "Exatamente UM MatchSet é salvo"
        def savedSets = matchSetRepository.findAll()
        assert savedSets.size() == 1

        and: "O MatchSet salvo contém os dados agregados corretos"
        def savedSet = savedSets[0]
        assert savedSet.finalResult == "2-1"
        assert savedSet.winningTeamName == "Time Vencedor"
        assert savedSet.battles.size() == 3
    }

    def "deve criar sets separados para partidas avulsas"() {
        given: "Um log de batalhas com duas partidas distintas que não devem ser agrupadas"
        def battleLogDTO = createSeparateBattlesLog()

        when: "O serviço processa o log"
        brawlDataService.processAndSaveBattleLog(battleLogDTO, [:]) // Mapa de times vazio

        then: "Dois MatchSets são criados, cada um com uma batalha"
        def savedSets = matchSetRepository.findAll()
        assert savedSets.size() == 2
        assert savedSets.every { it.battles.size() == 1 }
    }

    def "deve ser idempotente e não salvar sets duplicados"() {
        given: "Um log de batalhas"
        def battleLogDTO = createMockMD3Log()

        when: "O serviço processa o log pela primeira vez"
        brawlDataService.processAndSaveBattleLog(battleLogDTO, [:])

        then: "Um set é criado"
        assert matchSetRepository.count() == 1

        when: "O serviço processa EXATAMENTE o mesmo log uma segunda vez"
        brawlDataService.processAndSaveBattleLog(battleLogDTO, [:])

        then: "Nenhum set novo é salvo, a contagem permanece 1"
        matchSetRepository.count() == 1
    }

    def "deve reutilizar jogadores e brawlers que já existem no banco"() {
        given: "Um jogador e um brawler já existem no banco"
        playerRepository.save(new PlayerMODEL(tag: "P1", name: "PlayerUmPreExistente"))
        brawlerRepository.save(new BrawlerMODEL(name: "SHELLY"))

        def battleLogDTO = createMockMD3Log() // Este log contém P1 e SHELLY
        long initialPlayerCount = playerRepository.count()
        long initialBrawlerCount = brawlerRepository.count()

        when: "O serviço processa o log"
        brawlDataService.processAndSaveBattleLog(battleLogDTO, [:])

        then: "A contagem de jogadores e brawlers reflete a reutilização"
        // Mock tem P1 e P4. P1 já existe. Deve ser criado 1 novo. Total = 2
        assert playerRepository.count() == initialPlayerCount + 1
        // Mock tem SHELLY e COLT. SHELLY já existe. Deve ser criado 1 novo. Total = 2
        assert brawlerRepository.count() == initialBrawlerCount + 1
    }

    // --- MÉTODOS AUXILIARES COM DADOS COMPLETOS E ISOLADOS ---

    private BattleLogReceiveDTO createMockMD3Log() {
        def mapper = new ObjectMapper()
        def json = """
        {
          "items": [
            {
              "battleTime": "2025-08-03T16:00:00.000Z",
              "battle": { "result": "victory", "teams": [[{"tag":"#P1", "brawler":{"name":"SHELLY"}}],[{"tag":"#P4", "brawler":{"name":"COLT"}}]] }
            },
            {
              "battleTime": "2025-08-03T16:04:00.000Z",
              "battle": { "result": "defeat", "teams": [[{"tag":"#P1", "brawler":{"name":"SHELLY"}}],[{"tag":"#P4", "brawler":{"name":"COLT"}}]] }
            },
            {
              "battleTime": "2025-08-03T16:08:00.000Z",
              "battle": { "result": "victory", "teams": [[{"tag":"#P1", "brawler":{"name":"SHELLY"}}],[{"tag":"#P4", "brawler":{"name":"COLT"}}]] }
            }
          ]
        }
        """
        return mapper.readValue(json, BattleLogReceiveDTO.class)
    }

    private BattleLogReceiveDTO createSeparateBattlesLog() {
        def mapper = new ObjectMapper()
        def json = """
        {
          "items": [
            { "battleTime": "2025-08-03T18:00:00.000Z", "battle": { "result": "victory", "teams": [[{"tag":"#P1", "brawler":{"name":"SHELLY"}}],[{"tag":"#P4", "brawler":{"name":"COLT"}}]] } },
            { "battleTime": "2025-08-03T19:30:00.000Z", "battle": { "result": "defeat", "teams": [[{"tag":"#P2", "brawler":{"name":"NITA"}}],[{"tag":"#P5", "brawler":{"name":"BULL"}}]] } }
          ]
        }
        """
        return mapper.readValue(json, BattleLogReceiveDTO.class)
    }
}