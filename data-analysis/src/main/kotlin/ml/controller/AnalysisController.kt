package com.guilherme.ml.controller

import com.guilherme.ml.service.Analysis
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/analysis")
class AnalysisController (private val analysis: Analysis) {


    @GetMapping("/MostUsedBrawlers")
    fun mostUsedBrawlers() : List<Pair<String, Int>> {
analysis.mostUsedBrawlers()
    }
}