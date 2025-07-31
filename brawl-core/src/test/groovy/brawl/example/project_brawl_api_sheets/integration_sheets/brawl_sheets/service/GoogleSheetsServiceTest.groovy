package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service

import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.model.AppendValuesResponse
import com.google.api.services.sheets.v4.model.UpdateValuesResponse
import com.google.api.services.sheets.v4.model.ValueRange
import spock.lang.Specification
import spock.lang.Subject

class GoogleSheetsServiceTest extends Specification {

    // --- Dependências Mockadas ---
    // A simulação da API do Google precisa ser mais detalhada
    def sheetsGetRequest = Mock(Sheets.Spreadsheets.Values.Get)
    def sheetsUpdateRequest = Mock(Sheets.Spreadsheets.Values.Update)
    def sheetsAppendRequest = Mock(Sheets.Spreadsheets.Values.Append)
    def sheetsValues = Mock(Sheets.Spreadsheets.Values) {
        // Dizemos ao mock o que retornar para cada chamada de método
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
    def googleSheetsService = new GoogleSheetsService(sheets)

    def setup() {
        googleSheetsService.spreadsheetId = "TEST_SHEET_ID"
    }

    // ========== TESTES PARA ensureHeaderExists() ==========

    def "deve criar o cabeçalho se a planilha estiver vazia"() {
        given: "A API do Google retorna uma resposta vazia"
        // Simulamos que a execução do 'get' retorna um ValueRange vazio
        1 * sheetsGetRequest.execute() >> new ValueRange()

        when: "O método é chamado"
        googleSheetsService.ensureHeaderExists()

        then: "O método de atualização ('update') é chamado para criar o cabeçalho"
        // Verificamos se o 'update' e o 'execute' são chamados na cadeia
        1 * sheetsUpdateRequest.setValueInputOption("RAW") >> sheetsUpdateRequest
        1 * sheetsUpdateRequest.execute() >> new UpdateValuesResponse() // Retornamos um objeto real, não um mock
    }

    def "NÃO deve criar o cabeçalho se ele já existir"() {
        given: "A API do Google retorna um cabeçalho existente"
        def existingHeader = new ValueRange(values: [["Team Name", "Opponent Name"]])
        1 * sheetsGetRequest.execute() >> existingHeader

        when: "O método é chamado"
        googleSheetsService.ensureHeaderExists()

        then: "O método de atualização ('update') NUNCA é chamado"
        0 * sheetsUpdateRequest.execute()
    }



    def "deve anexar os dados à planilha se a lista não for vazia"() {
        given: "Uma lista de dados para ser enviada"
        def dataToAppend = [["Time A", "Time B", "2025-07-30..."]]

        when: "O método é chamado"
        googleSheetsService.appendDataToSheet(dataToAppend)

        then: "O método 'append' da API é chamado"
        1 * sheetsAppendRequest.setValueInputOption("RAW") >> sheetsAppendRequest
        1 * sheetsAppendRequest.execute() >> new AppendValuesResponse()
    }

    def "NÃO deve anexar dados se a lista for nula ou vazia"() {
        when: "O método é chamado com dados inválidos"
        googleSheetsService.appendDataToSheet([])
        googleSheetsService.appendDataToSheet(null)

        then: "O método 'append' da API NUNCA é chamado"
        0 * sheetsAppendRequest.execute()
    }
}