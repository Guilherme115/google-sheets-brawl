package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.model.*
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.repository.BattleMatchRepository
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class SheetExportServiceTest extends Specification {

    // --- Dependências Mockadas ---
    // Criamos versões falsas dos serviços externos para controlar o teste
    def googleSheetsService = Mock(GoogleSheetsService)
    def battleMatchRepository = Mock(BattleMatchRepository)

    // --- Classe Sob Teste ---
    // @Subject indica ao Spock que esta é a classe que estamos testando
    @Subject
    def sheetExportService = new SheetExportService(googleSheetsService, battleMatchRepository)

    // O @Unroll faz com que cada linha da tabela 'where' vire um teste separado no relatório
    @Unroll
    def "deve exportar para a planilha apenas quando houver novas batalhas (#scenario)"() {
        given: "O repositório está configurado para o cenário"
        // A variável 'battlesFromRepo' virá da tabela 'where' abaixo
        battleMatchRepository.findByBattleTimeGreaterThanOrderByBattleTimeAsc(_) >> battlesFromRepo
        // Resetamos o estado para cada execução
        sheetExportService.lastProcessedBattleTime = "2020-01-01T00:00:00.000Z"

        when: "A rotina de exportação é executada"
        sheetExportService.exportNewBattlesToSheet()

        then: "O serviço da planilha é chamado a quantidade correta de vezes"
        1 * googleSheetsService.ensureHeaderExists()
        // A variável 'timesCalled' virá da tabela 'where'
        timesCalled * googleSheetsService.appendDataToSheet(_)

        where: "testamos diferentes cenários"
        scenario             | battlesFromRepo      | timesCalled
        "sem batalhas novas" | []                   | 0
        "com batalhas novas" | createMockBattles()  | 1
    }

    def "deve transformar os dados corretamente, ordenando os times e jogadores"() {
        given: "Uma lista de batalhas mockadas"
        def mockBattles = createMockBattles()

        when: "Transformamos as batalhas em linhas para a planilha"
        def sheetRows = sheetExportService.transformBattlesToSheetRows(mockBattles)

        then: "A formatação e ordenação estão corretas"
        sheetRows.size() == 2

        // --- Validação da Partida 1 (CASA BRAWL vs. LOUD) ---
        def matchRow1 = sheetRows[0]
        assert matchRow1[0] == "CASA BRAWL"      // Time A (vem primeiro alfabeticamente)
        assert matchRow1[1] == "LOUD"            // Time B

        // Valida jogadores da Equipe A (CASA), que só tem 2 players no mock
        def teamA_players_data = [matchRow1[6], matchRow1[7], matchRow1[9], matchRow1[10], matchRow1[12], matchRow1[13]]
        assert teamA_players_data == ["Jogador CASA 1", "#CASA1", "Jogador CASA 2", "#CASA2", "N/A", "N/A"]

        // Valida jogadores da Equipe B (LOUD), que tem 3 players e são ordenados alfabeticamente
        def teamB_players_data = [matchRow1[15], matchRow1[16], matchRow1[18], matchRow1[19], matchRow1[21], matchRow1[22]]
        assert teamB_players_data == ["Edinho", "#LOUD2", "FireCrow", "#LOUD3", "KaioDog", "#LOUD1"]

        // --- Validação da Partida 2 (SK Gaming vs. Unknown) ---
        def matchRow2 = sheetRows[1]
        assert matchRow2[0] == "SK Gaming"       // Time A (TRACKED vem antes de UNKNOWN)
        assert matchRow2[1] == "Unknown"         // Time B
    }

    // ========== MÉTODO AUXILIAR PARA CRIAR DADOS DE TESTE ==========
    private List<BattleMatch> createMockBattles() {
        // --- Brawlers ---
        def brawler = new BrawlerMODEL(name: "ANY")

        // --- Jogadores ---
        def loudP1 = new PlayerMODEL(tag: "LOUD1", name: "KaioDog")
        def loudP2 = new PlayerMODEL(tag: "LOUD2", name: "Edinho")
        def loudP3 = new PlayerMODEL(tag: "LOUD3", name: "FireCrow")
        def casaP1 = new PlayerMODEL(tag: "CASA1", name: "Jogador CASA 1")
        def casaP2 = new PlayerMODEL(tag: "CASA2", name: "Jogador CASA 2")
        def skP1 = new PlayerMODEL(tag: "SK1", name: "Joker")
        def unknownP1 = new PlayerMODEL(tag: "RANDOM1", name: "Random Player")

        // --- Partida 1: LOUD vs CASA BRAWL ---
        def match1 = new BattleMatch(battleTime: "T1_LOUDxCASA", mode: "brawlBall", result: "victory", duration: 120)
        def teamLoud = new MatchTeamMODEL(nameTeam: "LOUD", teamType: TeamType.TRACKED, battle: match1)
        teamLoud.players = [
                new PlayerPerformanceMODEL(player: loudP1, brawler: brawler, team: teamLoud),
                new PlayerPerformanceMODEL(player: loudP2, brawler: brawler, team: teamLoud),
                new PlayerPerformanceMODEL(player: loudP3, brawler: brawler, team: teamLoud)
        ]
        def teamCasa = new MatchTeamMODEL(nameTeam: "CASA BRAWL", teamType: TeamType.TRACKED, battle: match1)
        teamCasa.players = [
                new PlayerPerformanceMODEL(player: casaP1, brawler: brawler, team: teamCasa),
                new PlayerPerformanceMODEL(player: casaP2, brawler: brawler, team: teamCasa)
        ]
        match1.teams = [teamLoud, teamCasa] // Ordem inicial no mock: LOUD primeiro

        // --- Partida 2: SK Gaming vs Time Desconhecido ---
        def match2 = new BattleMatch(battleTime: "T2_SKxUNKNOWN", mode: "knockout", result: "defeat", duration: 90)
        def teamSK = new MatchTeamMODEL(nameTeam: "SK Gaming", teamType: TeamType.TRACKED, battle: match2)
        teamSK.players = [new PlayerPerformanceMODEL(player: skP1, brawler: brawler, team: teamSK)]

        def teamUnknown = new MatchTeamMODEL(nameTeam: "Unknown", teamType: TeamType.UNKNOWN, battle: match2)
        teamUnknown.players = [new PlayerPerformanceMODEL(player: unknownP1, brawler: brawler, team: teamUnknown)]

        match2.teams = [teamSK, teamUnknown]

        return [match1, match2]
    }
}