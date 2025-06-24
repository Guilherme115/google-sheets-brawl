package com.guilherme.ml.controller

import com.guilherme.ml.service.Analysis
import com.guilherme.ml.service.SheetssService
import com.guilherme.ml.service.SyncService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/analysis")

class AnalysisController(
    private val syncService: SyncService

) {

    @GetMapping("/MostUsedBrawlers")
    fun mostUsedBrawlers(): List<Pair<String, Int>> {
        return syncService.mostUsedBrawlers()
    }


    // o fluxo vai se basear em salvar as analise no banco de dados.
}