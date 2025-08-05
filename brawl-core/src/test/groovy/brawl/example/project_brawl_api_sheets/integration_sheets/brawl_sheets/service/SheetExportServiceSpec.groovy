package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.*
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.MatchSetRepository
import spock.lang.Specification
import spock.lang.Subject

import java.time.LocalDateTime

class SheetExportServiceSpec extends Specification {

    // --- Mocks das Dependências ---
    // Os mocks são definidos corretamente
    def googleSheetsService = Mock(GoogleSheetsService)
    def matchSetRepository = Mock(MatchSetRepository)

    @Subject
    // A injeção está correta, garantindo que os tipos batem com o construtor
    def sheetExportService = new SheetExportService(googleSheetsService, matchSetRepository)

    def "deve transformar um set e exportá-lo corretamente"() {
        given: "Um mock de um set completo"
        def mockSet = createComplexMockSet()
        matchSetRepository.findBySetStartTimeGreaterThanOrderBySetStartTimeAsc(_) >> [mockSet]

        when: "O serviço de exportação é executado"
        sheetExportService.exportNewSetsToSheet()

        then: "O método de anexar do GoogleSheetsService é chamado com os dados corretos"
        1 * googleSheetsService.appendDataToSheet({ List<List<Object>> capturedRows ->
            // A asserção 'true' no final é implícita se nenhuma exceção for lançada
            assert capturedRows.size() == 1
            def row = capturedRows[0]

            // Validações principais
            assert row[0] == "Time Bravo"      // Team Name (time com TeamType.TRACKED vem primeiro)
            assert row[1] == "Time Charlie"    // Opponent Name
            assert row[4] == "victory"         // Result (do set, 'Time Bravo' venceu)
            assert row[6] == "2-1"             // Scoreboard
            assert row[7] == "Player A"        // Player #1 (ordenado alfabeticamente)
            assert row[8] == "#P_A"            // Player #1 Tag
            assert row[9] == "SHELLY"          // Brawler #1 (do primeiro jogo)
            assert row[10] == "Player B"       // Player #2
            assert row[11] == "#P_B"           // Player #2 Tag
            assert row[12] == "NITA"           // Brawler #2
        })
    }

    def "não deve exportar nada se não houver novos sets"() {
        given: "O repositório retorna uma lista vazia"
        matchSetRepository.findBySetStartTimeGreaterThanOrderBySetStartTimeAsc(_) >> []

        when: "O serviço de exportação é executado"
        sheetExportService.exportNewSetsToSheet()

        then: "O cabeçalho é verificado, mas nenhum dado é anexado"
        1 * googleSheetsService.ensureHeaderExists()
        0 * googleSheetsService.appendDataToSheet(_)
    }

    // Método auxiliar para criar dados de teste
    private MatchSet createComplexMockSet() {
        def pA = new PlayerMODEL(tag: "P_A", name: "Player A")
        def pB = new PlayerMODEL(tag: "P_B", name: "Player B")
        def o1 = new PlayerMODEL(tag: "O_1", name: "Opponent O1")
        def brawlers = ["SHELLY", "COLT", "SPIKE", "NITA", "DYNAMIKE"].collect { new BrawlerMODEL(name: it) }

        // A lógica do serviço ordena os times. TRACKED vem antes de UNKNOWN.
        def teamBravo = new MatchTeamMODEL(teamType: TeamType.TRACKED, nameTeam: "Time Bravo", players: [
                new PlayerPerformanceMODEL(player: pA, brawler: brawlers[0]), // Player A usa SHELLY
                new PlayerPerformanceMODEL(player: pB, brawler: brawlers[3])  // Player B usa NITA
        ])
        def teamCharlie = new MatchTeamMODEL(teamType: TeamType.UNKNOWN, nameTeam: "Time Charlie", players: [
                new PlayerPerformanceMODEL(player: o1, brawler: brawlers[4])
        ])

        def battle1 = new BattleMatch(
                battleTime: "2025-08-03T15:50:00Z",
                mode: "gemGrab", duration: 120,
                teams: [teamCharlie, teamBravo] // Desordenado de propósito, o serviço deve corrigir
        )
        def battle2 = new BattleMatch(battleTime: "2025-08-03T15:55:00Z")

        return new MatchSet(
                teamAName: "Time Bravo", teamBName: "Time Charlie",
                finalResult: "2-1", winningTeamName: "Time Bravo",
                setStartTime: LocalDateTime.now(), battles: [battle1, battle2]
        )
    }
}