package com.guilherme.ml.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.guilherme.ml.dto.MatchDTO
import org.springframework.stereotype.Service

@Service
class ConvertService(
    private val analysis: Analysis,
    private val sheetsService: SheetssService,
    private val matchDTO: MatchDTO
) {

    fun convertToDto(data : List<List<Any>>): List<MatchDTO> {
        val mapper = jacksonObjectMapper()

        val colunas = listOf(
            "teamName", "battleTime", "mode", "result", "duration",
            "player1", "player2", "player3",
            "brawlerP1", "brawlerP2", "brawlerP3",
            "opponent1", "opponent2", "opponent3",
            "opponentBrawler1", "opponentBrawler2", "opponentBrawler3"
        )

        val listaDeMapas: List<Map<String, Any>> = data.map { linha ->
            colunas.zip(linha).toMap()
        }

        val json = mapper.writeValueAsString(listaDeMapas)

        return mapper.readValue(json)
    }

        }


