package com.guilherme.ml.service

import com.guilherme.ml.dto.MatchDTO
import com.guilherme.ml.model.BrawlerUsage
import org.springframework.stereotype.Service
import kotlin.collections.listOf

@Service
class Analysis {
    fun mostUsedBrawlers(matches: List<MatchDTO>, topN: Int = 5): List<BrawlerUsage> {
        val allBrawlers = matches.flatMap { match ->
            listOf(
                match.brawlerP1,
                match.brawlerP2,
                match.brawlerP3,
                match.opponentBrawler1,
                match.opponentBrawler2,
                match.opponentBrawler3,
            )
        }
        val contagem = allBrawlers.groupingBy { it }.eachCount()

        return contagem
            .map { (brawler, count) -> BrawlerUsage( brawlerName = brawler, usageCount = count) }
            .sortedByDescending { it.usageCount }
            .take(topN)
    }
    fun teamWithMostVictory(matches: List<MatchDTO>, topN: Int = 5): List<Pair<String, Int>> {
        return matches
            .filter { it.result.equals("victory", ignoreCase = true)  }
            .groupingBy { it.teamName.uppercase() }
            .eachCount()
            .toList()
            .sortedByDescending { (_, value) -> value }
            .take(topN)
    }


    fun winRateByTeam(matches: List<MatchDTO>): List<Pair<String, Double>> {
        return matches
            .groupBy { it.teamName.uppercase() }
            .map { (team, partidasDoTime) ->
                val total = partidasDoTime.size
                val vitorias = partidasDoTime.count { it.result.equals("victory", ignoreCase = true) }
                team to (vitorias.toDouble() / total)
            }
            .sortedByDescending { it.second }
    }
    }

