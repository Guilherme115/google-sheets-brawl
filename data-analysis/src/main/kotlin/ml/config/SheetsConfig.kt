package com.guilherme.ml.config

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class SheetsConfig {
     var spreadsheetId = "SEU_ID_DA_PLANILHA_AQUI"
    private val APPLICATION_NAME = "Brawl Stars Sheets"
    private val JSON_FACTORY = JacksonFactory.getDefaultInstance()
    private val SCOPES = listOf(SheetsScopes.SPREADSHEETS_READONLY)

    //Por enquanto Indefinido
    private val CREDENTIALS_FILE_PATH = "credentials.json";

    fun getSheets() : Sheets {
        val httpTransport = GoogleNetHttpTransport.newTrustedTransport()

        val credentials = GoogleCredential.fromStream((FileInputStream(CREDENTIALS_FILE_PATH)))
            .createScoped(SCOPES);

        return Sheets.Builder(httpTransport, JSON_FACTORY, credentials)
            .setApplicationName(APPLICATION_NAME)
            .build();
    }

}