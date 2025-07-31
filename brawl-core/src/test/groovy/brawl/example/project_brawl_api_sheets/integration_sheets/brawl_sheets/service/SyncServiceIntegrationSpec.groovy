package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO.Battle
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO.Brawler
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO.Player
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.*
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.BattleMatchRepository
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.TeamRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.transaction.annotation.Transactional
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.spock.Testcontainers
import spock.lang.Specification
import org.testcontainers.junit.jupiter.Container

@SpringBootTest
@Testcontainers // Habilita a extensão Testcontainers para a classe
@Transactional
class SyncServiceIntegrationSpec extends Specification {

    @Autowired
    SyncService syncService
    @Autowired
    TeamRepository teamRepository
    @Autowired
    BattleMatchRepository battleMatchRepository

    @MockBean
    BrawlService brawlService

    // --- CORREÇÃO AQUI: Usamos @Container para gerenciar o ciclo de vida ---
    @Container
    static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
    // --- FIM DA CORREÇÃO ---

    @DynamicPropertySource
    static void databaseProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mysql.&getJdbcUrl)
        registry.add("spring.datasource.username", mysql.&getUsername)
        registry.add("spring.datasource.password", mysql.&getPassword)
        registry.add("spring.jpa.hibernate.ddl-auto", { "create-drop" })
    }

    def "syncBattleLogs deve buscar, processar e salvar uma nova batalha com todos os detalhes"() {
        given: "Um time de teste salvo no banco de dados"
        def player = new PlayerMODEL(tag: "TAG_TESTE", name: "Jogador Teste")
        def team = new TeamRegisterMODEL(name: "Time de Teste", players: [player] as Set)
        teamRepository.save(team)

        and: "O BrawlService (mockado) está configurado para retornar uma batalha válida"
        def batalhaValida = new BattleLogReceiveDTO.BattleLogInfo(
                battleTime: "T1_INTEGRATION_DETAILED",
                battle: new Battle(
                        mode: "gemGrab", type: "friendly", result: "victory", duration: 123,
                        teams: [
                                [new Player(tag: "#TAG_TESTE", name: "Jogador Teste", brawler: new Brawler(name: "Shelly"))],
                                [new Player(tag: "#RANDOM", name: "Oponente Aleatório", brawler: new Brawler(name: "Spike"))]
                        ]
                )
        )
        def rawLog = new BattleLogReceiveDTO(items: [batalhaValida])
        def teamTags = team.players*.tag

        brawlService.fetchPlayerBattleLog("TAG_TESTE") >> rawLog
        brawlService.getFilteredBattles(rawLog, teamTags) >> [batalhaValida]

        when: "A rotina de sincronização é executada"
        syncService.syncBattleLogs()

        then: "A batalha é salva com todos os detalhes no banco de dados"
        def savedMatches = battleMatchRepository.findAll()

        assert savedMatches.size() == 1
        def savedMatch = savedMatches[0]
        assert savedMatch.battleTime == "T1_INTEGRATION_DETAILED"

        assert savedMatches[0].teams.size() == 2
        def trackedTeam = savedMatches[0].teams.find { it.teamType == TeamType.TRACKED }
        assert trackedTeam.nameTeam == "Time de Teste"
        assert trackedTeam.players.size() == 1
        assert trackedTeam.players[0].player.name == "Jogador Teste"
    }
}