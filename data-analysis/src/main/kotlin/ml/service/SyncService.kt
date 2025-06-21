package com.guilherme.ml.service

import com.guilherme.ml.dto.MatchDTO

class SyncService (
    private val convert : ConvertService,
    private val analysis: Analysis,
    private val sheetssService: SheetssService) {

    fun config () : List <MatchDTO> {
        val lists = sheetssService.getData()
        return convert.convertToDto(lists)

    }
    fun mostUsedBrawlers () : List<Pair<String, Int>> {
        val list = config()
        return analysis.mostUsedBrawlers(list)

    }
    fun teamWithMostVictory () {
        val list = config()
        analysis.teamWithMostVictory(list)
    }

      fun winRateGlobal () {
        val list = config()
        analysis.winRateByTeam(list)
    }


}