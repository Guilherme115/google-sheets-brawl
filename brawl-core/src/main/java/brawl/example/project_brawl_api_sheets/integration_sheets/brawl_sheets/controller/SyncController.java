package brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.controller;

import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service.SheetExportService;
import brawl.example.project_brawl_api_sheets.integration_sheets.brawl_sheets.service.SyncService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sync")
@RequiredArgsConstructor
@Slf4j
public class SyncController {

    private final SyncService syncService;
    private final SheetExportService sheetExportService;

    /**
     * Dispara a rotina completa: busca batalhas da API e depois exporta os sets para a planilha.
     */
    @PostMapping("/all")
    public ResponseEntity<String> triggerFullSync() {
        log.info("Disparo manual da sincronização completa solicitado.");
        syncService.syncBattleLogs();
        // --- CORREÇÃO AQUI ---
        sheetExportService.exportNewSetsToSheet();
        // --- FIM DA CORREÇÃO ---
        return ResponseEntity.accepted().body("Sincronização completa iniciada em segundo plano.");
    }

    /**
     * Dispara apenas a rotina de buscar batalhas da API e salvar no banco.
     */
    @PostMapping("/battles")
    public ResponseEntity<String> triggerBattleLogSync() {
        log.info("Disparo manual da sincronização de batalhas solicitado.");
        syncService.syncBattleLogs();
        return ResponseEntity.accepted().body("Sincronização de batalhas iniciada em segundo plano.");
    }

    /**
     * Dispara apenas a rotina de exportar dados (sets) do banco para a planilha.
     */
    @PostMapping("/sheets")
    public ResponseEntity<String> triggerSheetExport() {
        log.info("Disparo manual da exportação para planilhas solicitado.");
        // --- CORREÇÃO AQUI ---
        sheetExportService.exportNewSetsToSheet();
        // --- FIM DA CORREÇÃO ---
        return ResponseEntity.accepted().body("Exportação para planilha iniciada em segundo plano.");
    }
}