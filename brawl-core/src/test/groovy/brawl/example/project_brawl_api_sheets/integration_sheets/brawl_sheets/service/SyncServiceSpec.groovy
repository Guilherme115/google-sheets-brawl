package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.dto.BattleLogReceiveDTO.Battle
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.PlayerMODEL
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.TeamRegisterMODEL
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.TeamRepository
import spock.lang.Specification
import spock.lang.Subject

class SyncServiceSpec extends Specification {

    // --- Dependências Mockadas ---
    def brawlService = Mock(BrawlService)
    def teamRegisterRepository = Mock(TeamRepository)
    def brawlDataService = Mock(BrawlDataService)

    // --- Classe Sob Teste ---
    @Subject
    def syncService = new SyncService(brawlService, teamRegisterRepository, brawlDataService)

    def "não deve fazer nada se não houver times no banco de dados"() {
        given:
        teamRegisterRepository.findAll() >> []

        when:
        syncService.syncBattleLogs()

        then:
        0 * brawlService._
        0 * brawlDataService._
    }

    def "deve processar cada time e evitar salvar batalhas duplicadas na mesma rodada"() {
        given:
        def playerA = new PlayerMODEL(tag: "TAG_A")
        def playerB = new PlayerMODEL(tag: "TAG_B")
        def teamA = new TeamRegisterMODEL(name: "Time A", players: [playerA] as Set)
        def teamB = new TeamRegisterMODEL(name: "Time B", players: [playerB] as Set)
        teamRegisterRepository.findAll() >> [teamA, teamB]

        def battle1_uniqueA = new BattleLogReceiveDTO.BattleLogInfo(
                battleTime: "T1_UNICA_A",
                battle: new Battle(mode: "gemGrab", type: "friendly", teams: [[]])
        )
        def battle2_duplicada = new BattleLogReceiveDTO.BattleLogInfo(
                battleTime: "T2_DUPLICADA",
                battle: new Battle(mode: "brawlBall", type: "tournament", teams: [[]])
        )
        def battle3_uniqueB = new BattleLogReceiveDTO.BattleLogInfo(
                battleTime: "T3_UNICA_B",
                battle: new Battle(mode: "heist", type: "friendly", teams: [[]])
        )

        def logDoTimeA = new BattleLogReceiveDTO(items: [battle1_uniqueA, battle2_duplicada])
        def logDoTimeB = new BattleLogReceiveDTO(items: [battle2_duplicada, battle3_uniqueB])

        brawlService.fetchPlayerBattleLog("TAG_A") >> logDoTimeA
        brawlService.fetchPlayerBattleLog("TAG_B") >> logDoTimeB

        brawlService.getFilteredBattles(logDoTimeA, _) >> logDoTimeA.getItems()
        brawlService.getFilteredBattles(logDoTimeB, _) >> logDoTimeB.getItems()

        // Criamos uma lista vazia para guardar os argumentos que o mock receber
        def capturedDtos = []

        when:
        syncService.syncBattleLogs()

        then:
        // A MÁGICA ESTÁ AQUI:
        // 1. Verificamos que o método foi chamado 2 vezes.
        // 2. A cada chamada (>>), a closure é executada.
        // 3. A closure pega o primeiro argumento (o DTO) e o adiciona à nossa lista 'capturedDtos'.
        2 * brawlDataService.processAndSaveBattleLog(_, _) >> { BattleLogReceiveDTO dto, Map map ->
            capturedDtos.add(dto)
        }

        and: "Os DTOs que guardamos na nossa lista estão corretos"
        // Agora, fora da verificação de interação, nós checamos o conteúdo da lista.
        // Usamos .find para não depender da ordem em que os times foram processados.
        def dtoDoTimeA = capturedDtos.find { it.items.any { it.battleTime == "T1_UNICA_A" } }
        def dtoDoTimeB = capturedDtos.find { it.items.any { it.battleTime == "T3_UNICA_B" } }

        assert dtoDoTimeA != null
        assert dtoDoTimeA.items.size() == 2
        assert dtoDoTimeA.items*.battleTime.containsAll(["T1_UNICA_A", "T2_DUPLICADA"])

        assert dtoDoTimeB != null
        assert dtoDoTimeB.items.size() == 1
        assert dtoDoTimeB.items[0].battleTime == "T3_UNICA_B"
    }

    def "deve pular times sem jogadores registrados"() {
        given:
        def teamSemJogadores = new TeamRegisterMODEL(name: "Time Vazio", players: [] as Set)
        teamRegisterRepository.findAll() >> [teamSemJogadores]

        when:
        syncService.syncBattleLogs()

        then:
        0 * brawlService.fetchPlayerBattleLog(_)
    }
}