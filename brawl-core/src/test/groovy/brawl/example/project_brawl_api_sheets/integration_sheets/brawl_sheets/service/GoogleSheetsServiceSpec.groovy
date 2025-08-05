package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.AppendValuesResponse
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import spock.lang.Specification
import spock.lang.Subject
import spock.lang.Unroll

class GoogleSheetsServiceSpec extends Specification {

    // --- Mocks para a API do Google (simulando a estrutura) ---
    def sheetsGetRequest = Mock(Sheets.Spreadsheets.Values.Get)
    def sheetsUpdateRequest = Mock(Sheets.Spreadsheets.Values.Update)
    def sheetsAppendRequest = Mock(Sheets.Spreadsheets.Values.Append)

    // Mock principal que encadeia as chamadas
    def sheetsValues = Mock(Sheets.Spreadsheets.Values) {
        get(_, _) >> sheetsGetRequest
        update(_, _, _) >> sheetsUpdateRequest
        append(_, _, _) >> sheetsAppendRequest
    }
    def sheetsSpreadsheets = Mock(Sheets.Spreadsheets) {
        values() >> sheetsValues
    }
    def sheets = Mock(Sheets) {
        spreadsheets() >> sheetsSpreadsheets
    }

    @Subject
    // O serviço sendo testado, injetado com o mock principal
    def googleSheetsService = new GoogleSheetsService(sheets)

    // O cabeçalho esperado, espelhando o método privado do serviço
    def expectedHeader = [
            "Team Name", "Opponent Name", "Battle Time", "Mode", "Result", "Duration", "Scoreboard",
            "Player #1", "Tag #1", "Brawler #1", "Player #2", "Tag #2", "Brawler #2",
            "Player #3", "Tag #3", "Brawler #3", "Opponent #1", "Opponent Tag #1", "Opponent Brawler #1",
            "Opponent #2", "Opponent Tag #2", "Opponent Brawler #2", "Opponent #3", "Opponent Tag #3", "Opponent Brawler #3"
    ]

    def setup() {
        // Define o ID da planilha para os testes
        googleSheetsService.spreadsheetId = "TEST_SHEET_ID"
    }

    // --- Testes para ensureHeaderExists() ---

    def "deve criar o cabeçalho se a planilha estiver vazia"() {
        given: "A API do Google retorna uma resposta sem valores (planilha vazia)"
        1 * sheetsGetRequest.execute() >> new ValueRange()

        when: "O método para garantir o cabeçalho é chamado"
        googleSheetsService.ensureHeaderExists()

        then: "Uma chamada de atualização é feita para inserir o cabeçalho"
        // Garante que a chamada encadeada do SDK do Google funcione
        1 * sheetsUpdateRequest.setValueInputOption("RAW") >> sheetsUpdateRequest
        1 * sheetsUpdateRequest.execute() >> new UpdateValuesResponse()
    }

    def "NÃO deve criar o cabeçalho se ele já existir e for igual"() {
        given: "A API do Google retorna exatamente o cabeçalho esperado"
        def existingHeader = new ValueRange().setValues([expectedHeader])
        1 * sheetsGetRequest.execute() >> existingHeader

        when: "O método para garantir o cabeçalho é chamado"
        googleSheetsService.ensureHeaderExists()

        then: "Nenhuma chamada de atualização é feita"
        0 * sheetsUpdateRequest.execute()
    }

    def "DEVE atualizar o cabeçalho se ele existir mas for diferente"() {
        given: "A API do Google retorna um cabeçalho antigo/diferente"
        def oldHeader = new ValueRange().setValues([["Coluna Antiga 1", "Coluna Antiga 2"]])
        1 * sheetsGetRequest.execute() >> oldHeader

        when: "O método de garantir o cabeçalho é chamado"
        googleSheetsService.ensureHeaderExists()

        then: "Uma chamada de atualização é feita para corrigir o cabeçalho"
        1 * sheetsUpdateRequest.setValueInputOption("RAW") >> sheetsUpdateRequest
        1 * sheetsUpdateRequest.execute() >> new UpdateValuesResponse()
    }

    // --- Testes para appendDataToSheet() ---

    def "deve anexar dados quando uma lista válida é fornecida"() {
        given: "Uma lista de linhas para adicionar"
        def data = [["Linha 1"], ["Linha 2"]]

        when: "O método de anexar dados é chamado"
        googleSheetsService.appendDataToSheet(data)

        then: "A API de 'append' é chamada uma vez para salvar os dados"
        // --- CORREÇÃO AQUI ---
        // A API do Google tem um padrão "fluent", então cada chamada retorna o próprio objeto.
        // Precisamos simular isso no mock para que o ".execute()" não dê NullPointerException.
        1 * sheetsAppendRequest.setValueInputOption("RAW") >> sheetsAppendRequest
        1 * sheetsAppendRequest.execute() >> new AppendValuesResponse()
    }

    // Usando @Unroll para testar múltiplos cenários (null e lista vazia) de uma vez
    @Unroll
    def "NÃO deve anexar dados se a lista for #description"() {
        when: "O método de anexar é chamado com dados inválidos"
        googleSheetsService.appendDataToSheet(data)

        then: "A API de 'append' nunca é chamada"
        0 * sheetsAppendRequest.execute()

        where:
        description | data
        "nula"      | null
        "vazia"     | []
    }
}