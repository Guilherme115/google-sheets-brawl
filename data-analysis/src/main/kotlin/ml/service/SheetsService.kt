package com.guilherme.ml.service

import com.google.api.services.sheets.v4.Sheets
import com.guilherme.ml.config.SheetsConfig
import com.google.api.services.sheets.v4.model.ValueRange;
import org.springframework.stereotype.Service

//Serviço que vai ser responsavel por pegar os dados do google sheets e colcoar aquio dentro do nosso querido filtro
@Service
class SheetssService(private val sheetsConfig: SheetsConfig) {

    val sheetService = sheetsConfig.getSheets()
    val spreadsheetId = sheetsConfig.spreadsheetId

    fun getData(sheetsService: Sheets, spreadsheetId: String, range: String): List<List<Any>> {
        val response: ValueRange = sheetsService.spreadsheets().values()
            .get(spreadsheetId, range)
            .execute()
        return response.getValues() ?: emptyList()
    }


}


//val sheetsConfig = SheetsConfig()
//val sheetsService = sheetsConfig.getSheetsService()
//
//val response = sheetsService.spreadsheets().values()
//    .get(sheetsConfig.spreadsheetId, "Sheet1!A1:D10")
//    .execute()
//
//val values = response.getValues()
//println(values)